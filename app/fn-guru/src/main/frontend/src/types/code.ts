export interface Code {
    id: string;
    value: string;
    version: number;
}

export type CodeCallback = (version: number) => void