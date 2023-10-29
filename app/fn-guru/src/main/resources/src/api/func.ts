import {
    ApiFunc,
    ApiFuncList,
} from "./types";

import {authorizedDefaultHeaders} from "./shared";

export interface ListFuncQuery {
    limit: number;
}

export async function listFunc(query: ListFuncQuery): Promise<ApiFuncList> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/funcs`, {
        headers: authorizedDefaultHeaders(),
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiFuncList;
}

export interface SubmitCreateFunc {
    name: string;
    namespaceId: string;
}

export async function createFunc(cmd: SubmitCreateFunc): Promise<object> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/namespaces/${cmd.namespaceId}/funcs`, {
        headers: authorizedDefaultHeaders(),
        method: "POST",
        body: JSON.stringify({
                name: cmd.name,
                inputs: {},
                code: ""
            }
        )
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json();
}


export interface SubmitUpdateFuncReq {
    name?: string;
    code?: string;
}

export async function updateFunc(funcId: string, req: SubmitUpdateFuncReq): Promise<object> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/funcs/${funcId}`, {
        headers: authorizedDefaultHeaders(),
        method: "PATCH",
        body: JSON.stringify({
                name: req.name,
                inputs: {},
                code: req.code
            }
        )
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json();
}

export interface SubmitInvokeFuncReq {
    correlationId?: string;
    inputs?: object;
}

export async function invokeFunc(funcId: string, req: SubmitInvokeFuncReq): Promise<object> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/funcs/${funcId}/invoke`, {
        headers: authorizedDefaultHeaders(),
        method: "POST",
        body: JSON.stringify(req)
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json();
}


export async function getFunction(id: string): Promise<ApiFunc> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/funcs/${id}`, {
        headers: authorizedDefaultHeaders(),
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiFunc;
}