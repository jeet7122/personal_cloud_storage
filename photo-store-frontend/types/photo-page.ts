import {Photo} from "@/types/photo";

export type PhotoPageResponse = {
    content: Photo[];
    page: number;
    size: number;
    totalPages: number;
    totalElements: number;
    hasNext: boolean;
    hasPrevious: boolean;
};