import {
    ApiNamespace, ApiNamespaceList,
} from "./types";

import {authorizedDefaultHeaders} from "./shared";

export interface ListNamespaceQuery {
    limit: number;
    groupId: string;
}

export async function listNamespace(query: ListNamespaceQuery): Promise<ApiNamespaceList> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/groups/${query.groupId}/namespaces`, {
        headers: authorizedDefaultHeaders(),
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
    groupId: string;
}

export async function createNamespace(req: ApiNamespaceCreateReq): Promise<object> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/groups/${req.groupId}/namespaces`, {
        headers: authorizedDefaultHeaders(),
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
    return await response.json();
}

export async function getNamespace(id: string): Promise<ApiNamespace> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/namespaces/${id}`, {
        headers: authorizedDefaultHeaders(),
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiNamespace;
}