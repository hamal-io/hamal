import {defaultHeaders} from "./shared";
import {ApiExecList} from "./types";

export interface ListExecsQuery {
    funcId: string;
    limit: number;
}

export async function listExecs({funcId, limit}: ListExecsQuery): Promise<ApiExecList> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/execs?func_ids=${funcId}&limit=${limit}`, {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiExecList;
}