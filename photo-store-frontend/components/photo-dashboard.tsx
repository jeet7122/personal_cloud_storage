"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useAuth } from "@clerk/nextjs";
import { useDropzone } from "react-dropzone";
import axios from "axios";
import { Photo } from "@/types/photo";
import { SelectedFile } from "@/types/selected-file";
import { PhotoPageResponse } from "@/types/photo-page";

type PhotoWithPreview = Photo & {
    previewUrl?: string;
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL!;

export default function PhotoDashboard() {
    const { getToken, isLoaded, isSignedIn } = useAuth();

    const [selectedFiles, setSelectedFiles] = useState<SelectedFile[]>([]);
    const [message, setMessage] = useState("");
    const [photos, setPhotos] = useState<PhotoWithPreview[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isUploading, setIsUploading] = useState(false);
    const [isFetchingMore, setIsFetchingMore] = useState(false);
    const [deletingId, setDeletingId] = useState<string | null>(null);

    const [page, setPage] = useState(0);
    const [pageSize] = useState(20);
    const [hasNext, setHasNext] = useState(false);

    const observerRef = useRef<IntersectionObserver | null>(null);

    const selectedFilesLabel = useMemo(() => {
        if (selectedFiles.length === 0) return "";
        if (selectedFiles.length === 1) return selectedFiles[0].file.name;
        return `${selectedFiles.length} files selected`;
    }, [selectedFiles]);

    const getAuthHeaders = async () => {
        const token = await getToken();

        if (!token) {
            throw new Error("No auth token available");
        }

        return {
            Authorization: `Bearer ${token}`,
        };
    };

    const createSelectedFiles = (files: File[]): SelectedFile[] => {
        return files.map((file) => ({
            id: `${file.name}-${file.size}-${file.lastModified}-${crypto.randomUUID()}`,
            file,
            previewUrl: URL.createObjectURL(file),
            progress: 0,
            status: "pending" as const,
        }));
    };

    const updateSelectedFile = (id: string, updates: Partial<SelectedFile>) => {
        setSelectedFiles((prev) =>
            prev.map((item) => (item.id === id ? { ...item, ...updates } : item))
        );
    };

    const onDrop = useCallback((acceptedFiles: File[]) => {
        if (acceptedFiles.length === 0) return;

        const mappedFiles = createSelectedFiles(acceptedFiles);
        setSelectedFiles((prev) => [...prev, ...mappedFiles]);
        setMessage("");
    }, []);

    const { getRootProps, getInputProps, isDragActive, open } = useDropzone({
        onDrop,
        multiple: true,
        noClick: true,
        accept: {
            "image/png": [".png"],
            "image/jpeg": [".jpg", ".jpeg"],
            "image/webp": [".webp"],
        },
        disabled: isUploading,
    });

    const fetchPhotos = async (pageNumber = 0, append = false) => {
        try {
            if (append) {
                setIsFetchingMore(true);
            } else {
                setIsLoading(true);
                setMessage("");
            }

            const headers = await getAuthHeaders();

            const res = await fetch(
                `${API_BASE_URL}/photos?page=${pageNumber}&size=${pageSize}`,
                { headers }
            );

            if (!res.ok) {
                throw new Error("Failed to fetch photos");
            }

            const data: PhotoPageResponse = await res.json();
            const incomingPhotos = data.content;

            if (append) {
                setPhotos((prev) => [...prev, ...incomingPhotos]);
            } else {
                setPhotos(incomingPhotos);
            }

            setPage(data.page);
            setHasNext(data.hasNext);
        } catch {
            setMessage("Failed to load photos");
        } finally {
            setIsLoading(false);
            setIsFetchingMore(false);
        }
    };

    useEffect(() => {
        if (!isLoaded || !isSignedIn) return;
        fetchPhotos(0, false);
    }, [isLoaded, isSignedIn]);

    useEffect(() => {
        return () => {
            selectedFiles.forEach((item) => {
                URL.revokeObjectURL(item.previewUrl);
            });
        };
    }, []);

    const lastElementRef = useCallback(
        (node: HTMLDivElement | null) => {
            if (isLoading || isFetchingMore) return;
            if (observerRef.current) observerRef.current.disconnect();

            observerRef.current = new IntersectionObserver((entries) => {
                if (entries[0].isIntersecting && hasNext) {
                    fetchPhotos(page + 1, true);
                }
            });

            if (node) observerRef.current.observe(node);
        },
        [hasNext, isLoading, isFetchingMore, page]
    );

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const files = Array.from(event.target.files || []);
        if (files.length === 0) return;

        const mappedFiles = createSelectedFiles(files);
        setSelectedFiles((prev) => [...prev, ...mappedFiles]);
        setMessage("");

        event.target.value = "";
    };

    const removeSelectedFile = (id: string) => {
        setSelectedFiles((prev) => {
            const fileToRemove = prev.find((item) => item.id === id);

            if (fileToRemove) {
                URL.revokeObjectURL(fileToRemove.previewUrl);
            }

            return prev.filter((item) => item.id !== id);
        });
    };

    const clearSelectedFiles = () => {
        selectedFiles.forEach((item) => {
            URL.revokeObjectURL(item.previewUrl);
        });
        setSelectedFiles([]);
    };

    const handleUpload = async () => {
        if (selectedFiles.length === 0) {
            setMessage("Please select at least one file");
            return;
        }

        try {
            setIsUploading(true);
            setMessage("");

            const headers = await getAuthHeaders();

            let uploadedCount = 0;
            let duplicateCount = 0;
            let failedCount = 0;

            for (const item of selectedFiles) {
                try {
                    updateSelectedFile(item.id, {
                        status: "uploading",
                        progress: 0,
                    });

                    const formData = new FormData();
                    formData.append("files", item.file);

                    const response = await axios.post(`${API_BASE_URL}/photos/upload`, formData, {
                        headers,
                        onUploadProgress: (progressEvent) => {
                            const total = progressEvent.total ?? item.file.size;
                            const percent = Math.min(
                                100,
                                Math.round((progressEvent.loaded * 100) / total)
                            );

                            updateSelectedFile(item.id, {
                                progress: percent,
                            });
                        },
                    });



                    const uploadedPhotos = response.data;
                    const uploadedArray = Array.isArray(uploadedPhotos)
                        ? uploadedPhotos
                        : [uploadedPhotos];

                    const isDuplicate = uploadedArray.some((photo: Photo) => photo.duplicate);

                    if (isDuplicate) {
                        duplicateCount++;
                    } else {
                        uploadedCount++;
                    }

                    updateSelectedFile(item.id, {
                        progress: 100,
                        status: "done",
                    });
                } catch {
                    failedCount++;
                    updateSelectedFile(item.id, {
                        status: "error",
                    });
                }
            }

            if (uploadedCount > 0 && duplicateCount > 0 && failedCount === 0) {
                setMessage(`${uploadedCount} uploaded, ${duplicateCount} already existed.`);
            } else if (uploadedCount > 0 && failedCount > 0) {
                setMessage(`${uploadedCount} uploaded successfully, ${failedCount} failed.`);
            } else if (duplicateCount > 0 && uploadedCount === 0 && failedCount === 0) {
                setMessage("All selected images already exist.");
            } else if (uploadedCount > 0) {
                setMessage(`${uploadedCount} image(s) uploaded successfully!`);
            } else if (failedCount > 0) {
                setMessage(`${failedCount} upload(s) failed.`);
            }

            await fetchPhotos(0, false);

            setTimeout(() => {
                clearSelectedFiles();
            }, 1000);
        } catch {
            setMessage("Upload failed");
        } finally {
            setIsUploading(false);
        }
    };

    const deletePhoto = async (id: string) => {
        try {
            setDeletingId(id);
            setMessage("");

            const headers = await getAuthHeaders();

            const res = await fetch(`${API_BASE_URL}/photos/${id}`, {
                method: "DELETE",
                headers,
            });

            if (!res.ok) {
                throw new Error("Delete failed");
            }

            setMessage("Photo deleted successfully");
            await fetchPhotos(0, false);
        } catch {
            setMessage("Failed to delete photo");
        } finally {
            setDeletingId(null);
        }
    };

    const openPhoto = async (id: string) => {
        try {
            const headers = await getAuthHeaders();

            const res = await fetch(`${API_BASE_URL}/photos/${id}/url`, {
                headers,
            });

            if (!res.ok) {
                throw new Error("Failed to get photo URL");
            }

            const data = await res.json();
            window.open(data.url, "_blank");
        } catch {
            setMessage("Failed to open photo");
        }
    };

    const formatFileSize = (size: number) => {
        if (size < 1024) return `${size} B`;
        if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
        return `${(size / (1024 * 1024)).toFixed(1)} MB`;
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString(undefined, {
            year: "numeric",
            month: "short",
            day: "numeric",
        });
    };

    if (!isLoaded) {
        return <div className="p-8 text-sm text-gray-600">Loading authentication...</div>;
    }

    return (
        <main className="min-h-screen bg-[#f8f9fa] text-gray-900">
            <section className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
                <div className="mb-8 rounded-2xl border border-gray-200 bg-white p-5 shadow-sm">
                    <div className="mb-5">
                        <h1 className="text-2xl font-semibold tracking-tight">Personal Cloud</h1>
                        <p className="mt-1 text-sm text-gray-600">
                            Store, preview, and manage your images.
                        </p>
                    </div>

                    <div
                        {...getRootProps()}
                        className={`rounded-2xl border-2 border-dashed px-6 py-10 text-center transition ${
                            isDragActive
                                ? "border-blue-500 bg-blue-50"
                                : "border-gray-300 bg-gray-50 hover:border-blue-400 hover:bg-blue-50/60"
                        } ${isUploading ? "cursor-not-allowed opacity-60" : "cursor-pointer"}`}
                    >
                        <input {...getInputProps()} />
                        <p className="text-sm font-medium text-gray-700">
                            {isDragActive ? "Drop your images here..." : "Drag and drop images here"}
                        </p>
                        <p className="mt-2 text-xs text-gray-500">
                            PNG, JPG, WEBP • multiple files supported
                        </p>

                        <div className="mt-4">
                            <button
                                type="button"
                                onClick={open}
                                disabled={isUploading}
                                className="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 disabled:opacity-50"
                            >
                                Choose files
                            </button>
                        </div>

                        <div className="mt-3">
                            <input
                                type="file"
                                multiple
                                accept="image/png,image/jpeg,image/webp"
                                onChange={handleFileChange}
                                disabled={isUploading}
                                className="hidden"
                            />
                        </div>
                    </div>

                    <div className="mt-4 flex justify-end">
                        <button
                            onClick={handleUpload}
                            disabled={selectedFiles.length === 0 || isUploading}
                            className="rounded-lg bg-blue-600 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            {isUploading ? "Uploading..." : "Upload"}
                        </button>
                    </div>
                </div>

                {selectedFiles.length > 0 && (
                    <div className="mb-6 rounded-2xl border border-blue-200 bg-blue-50 p-4">
                        <div className="mb-3 flex items-center justify-between">
                            <p className="text-sm font-medium text-blue-900">{selectedFilesLabel}</p>

                            <button
                                onClick={clearSelectedFiles}
                                disabled={isUploading}
                                className="rounded-md border border-blue-300 bg-white px-3 py-1.5 text-xs font-medium text-blue-700 hover:bg-blue-100 disabled:opacity-50"
                            >
                                Clear all
                            </button>
                        </div>

                        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                            {selectedFiles.map((item) => (
                                <div
                                    key={item.id}
                                    className="overflow-hidden rounded-xl border border-blue-200 bg-white shadow-sm"
                                >
                                    <div className="aspect-square overflow-hidden bg-gray-100">
                                        <img
                                            src={item.previewUrl}
                                            alt={item.file.name}
                                            className="h-full w-full object-cover"
                                        />
                                    </div>

                                    <div className="p-3">
                                        <p className="truncate text-sm font-medium text-gray-900">
                                            {item.file.name}
                                        </p>
                                        <p className="mt-1 text-xs text-gray-500">
                                            {formatFileSize(item.file.size)}
                                        </p>

                                        <div className="mt-3">
                                            <div className="h-2 w-full overflow-hidden rounded-full bg-gray-200">
                                                <div
                                                    className={`h-full rounded-full transition-all duration-300 ${
                                                        item.status === "error"
                                                            ? "bg-red-500"
                                                            : item.status === "done"
                                                                ? "bg-green-500"
                                                                : "bg-blue-600"
                                                    }`}
                                                    style={{ width: `${item.progress}%` }}
                                                />
                                            </div>

                                            <div className="mt-2 flex items-center justify-between text-xs">
                                                <span className="text-gray-500">{item.progress}%</span>
                                                <span
                                                    className={`font-medium ${
                                                        item.status === "error"
                                                            ? "text-red-600"
                                                            : item.status === "done"
                                                                ? "text-green-600"
                                                                : item.status === "uploading"
                                                                    ? "text-blue-600"
                                                                    : "text-gray-500"
                                                    }`}
                                                >
                          {item.status === "pending" && "Pending"}
                                                    {item.status === "uploading" && "Uploading"}
                                                    {item.status === "done" && "Done"}
                                                    {item.status === "error" && "Failed"}
                        </span>
                                            </div>
                                        </div>

                                        <button
                                            onClick={() => removeSelectedFile(item.id)}
                                            disabled={isUploading}
                                            className="mt-3 rounded-md border border-red-200 px-3 py-1.5 text-xs font-medium text-red-600 hover:bg-red-50 disabled:opacity-50"
                                        >
                                            Remove
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {message && (
                    <div className="mb-6 rounded-xl border border-gray-200 bg-white px-4 py-3 text-sm font-medium text-gray-800 shadow-sm">
                        {message}
                    </div>
                )}

                <div className="mb-5 flex items-center justify-between">
                    <div>
                        <h2 className="text-xl font-semibold">Your Photos</h2>
                        <p className="mt-1 text-sm text-gray-500">
                            {photos.length} {photos.length === 1 ? "photo" : "photos"}
                        </p>
                    </div>
                </div>

                {isLoading ? (
                    <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                        {Array.from({ length: 8 }).map((_, index) => (
                            <div
                                key={index}
                                className="overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm"
                            >
                                <div className="aspect-square animate-pulse bg-gray-200" />
                                <div className="space-y-2 p-3">
                                    <div className="h-4 animate-pulse rounded bg-gray-200" />
                                    <div className="h-3 w-2/3 animate-pulse rounded bg-gray-200" />
                                </div>
                            </div>
                        ))}
                    </div>
                ) : photos.length === 0 ? (
                    <div className="rounded-2xl border border-dashed border-gray-300 bg-white px-6 py-14 text-center shadow-sm">
                        <p className="text-sm text-gray-600">No photos uploaded yet.</p>
                    </div>
                ) : (
                    <>
                        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                            {photos.map((photo, index) => {
                                const isLast = index === photos.length - 1;

                                return (
                                    <div
                                        key={photo.id}
                                        ref={isLast ? lastElementRef : null}
                                        className="group overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm transition hover:shadow-md"
                                    >
                                        <div
                                            className="relative aspect-square cursor-pointer overflow-hidden bg-gray-100"
                                            onClick={() => openPhoto(photo.id)}
                                        >
                                            {photo.previewUrl ? (
                                                <img
                                                    src={photo.previewUrl}
                                                    alt={photo.originalFileName}
                                                    className="h-full w-full object-cover transition duration-300 group-hover:scale-[1.03]"
                                                />
                                            ) : (
                                                <div className="flex h-full w-full items-center justify-center text-sm text-gray-400">
                                                    Preview unavailable
                                                </div>
                                            )}

                                            <div className="pointer-events-none absolute inset-0 bg-black/0 transition group-hover:bg-black/10" />

                                            <div className="absolute right-2 top-2 flex gap-2 opacity-0 transition group-hover:opacity-100">
                                                <button
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        openPhoto(photo.id);
                                                    }}
                                                    className="rounded-md bg-white/95 px-3 py-1.5 text-xs font-medium text-gray-800 shadow"
                                                >
                                                    View
                                                </button>

                                                <button
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        deletePhoto(photo.id);
                                                    }}
                                                    disabled={deletingId === photo.id}
                                                    className="rounded-md bg-red-600 px-3 py-1.5 text-xs font-medium text-white shadow disabled:opacity-50"
                                                >
                                                    {deletingId === photo.id ? "Deleting..." : "Delete"}
                                                </button>
                                            </div>
                                        </div>

                                        <div className="p-3">
                                            <p className="truncate text-sm font-medium text-gray-900">
                                                {photo.originalFileName}
                                            </p>
                                            <div className="mt-1 flex items-center justify-between text-xs text-gray-500">
                                                <span>{formatFileSize(photo.size)}</span>
                                                <span>{formatDate(photo.uploadedAt)}</span>
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>

                        {isFetchingMore && (
                            <div className="mt-6 text-center text-sm text-gray-500">
                                Loading more photos...
                            </div>
                        )}

                        {!hasNext && photos.length > 0 && !isLoading && (
                            <div className="mt-6 text-center text-sm text-gray-400">
                                You’ve reached the end.
                            </div>
                        )}
                    </>
                )}
            </section>
        </main>
    );
}