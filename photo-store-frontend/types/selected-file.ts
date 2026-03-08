export type SelectedFile = {
    id: string;
    file: File;
    previewUrl: string;
    progress: number;
    status: "pending" | "error" | "uploading" | "done" ;
}