import {
    ApiFunc,
    ApiFuncList,
    ApiSubmittedReqWithId
} from "./types";

import {defaultHeaders} from "./shared";

export interface ListFuncsQuery {
    limit: number;
}

export async function listFuncs(query: ListFuncsQuery): Promise<ApiFuncList> {
    const response = await fetch("http://localhost:8008/v1/funcs", {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiFuncList;
}

export interface SubmitCreateFuncReq {
    name: string;
}

export async function createFunc(req: SubmitCreateFuncReq): Promise<ApiSubmittedReqWithId> {
    const response = await fetch("http://localhost:8008/v1/groups/1/funcs", {
        headers: defaultHeaders,
        method: "POST",
        body: JSON.stringify({
                name: req.name,
                inputs: {},
                code: ""
            }
        )
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiSubmittedReqWithId;
}


export interface SubmitUpdateFuncReq {
    name?: string;
    code?: string;
}

export async function updateFunc(funcId: string, req: SubmitUpdateFuncReq): Promise<ApiSubmittedReqWithId> {
    const response = await fetch(`http://localhost:8008/v1/funcs/${funcId}`, {
        headers: defaultHeaders,
        method: "PUT",
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
    return await response.json() as ApiSubmittedReqWithId;
}

export interface SubmitInvokeFuncReq {
    correlationId?: string;
    inputs?: object;
}

export async function invokeFunc(funcId: string, req: SubmitInvokeFuncReq): Promise<ApiSubmittedReqWithId> {
    const response = await fetch(`http://localhost:8008/v1/funcs/${funcId}/invoke`, {
        headers: defaultHeaders,
        method: "POST",
        body: JSON.stringify(req)
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiSubmittedReqWithId;
}


export async function getFunction(id: string): Promise<ApiFunc> {
    const response = await fetch(`http://localhost:8008/v1/funcs/${id}`, {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiFunc;
}