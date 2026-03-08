export type Photo = {
    id: string;
    originalFileName: string;
    contentType: string;
    size: number;
    uploadedAt: string,
    duplicate?: boolean;
}