import {
    ApiFunc,
    ApiFuncList, ApiNamespace, ApiNamespaceList,
    ApiSubmittedReqWithId
} from "./types";

import {defaultHeaders} from "./shared";

export interface ListNamespaceQuery {
    limit: number;
}

export async function listNamespace(query: ListNamespaceQuery): Promise<ApiNamespaceList> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/groups/1/namespaces`, {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiNamespaceList;
}

export interface ApiNamespaceCreateReq {
    name: string;
}

export async function createNamespace(req: ApiNamespaceCreateReq): Promise<ApiSubmittedReqWithId> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/groups/1/namespaces`, {
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

export async function getNamespace(id: string): Promise<ApiNamespace> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/namespaces/${id}`, {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiNamespace;
}