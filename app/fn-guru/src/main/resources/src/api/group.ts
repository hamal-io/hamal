import {authorizedDefaultHeaders} from "./shared";

export interface ApiGroupList {
    groups: Array<ApiGroupSimple>;
}

export interface ApiGroupSimple {
    id: string;
    name: string;
}

export interface ApiGroup {
    id: string;
    name: string;
}


export interface ListGroupQuery {
    limit: number;
}

export async function listGroup(query: ListGroupQuery): Promise<ApiGroupList> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/groups`, {
        headers: authorizedDefaultHeaders(),
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiGroupList;
}

export interface ApiGroupCreateReq {
    name: string;
}

export async function getGroup(id: string): Promise<ApiGroup> {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/groups/${id}`, {
        headers: authorizedDefaultHeaders(),
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiGroup;
}