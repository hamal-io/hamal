import {defaultHeaders} from "./shared";
import {ApiExec, ApiExecCode, ApiExecList} from "./types";

export interface ListExecsQuery {
    groupId?: string;
    funcId?: string;
    limit: number;
}

export async function listExecs({funcId, groupId, limit}: ListExecsQuery): Promise<ApiExecList> {
    if (groupId !== undefined) {
        const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/execs?group_ids=${groupId}&limit=${limit}`, {
            headers: defaultHeaders,
            method: "GET",
        })
        if (!response.ok) {
            const message = `Request submission failed: ${response.status} - ${response.statusText}`;
            throw new Error(message);
        }
        return await response.json() as ApiExecList;
    } else {

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

}

export async function getExec(execId: string): Promise<ApiExec> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/execs/${execId}`, {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiExec;
}