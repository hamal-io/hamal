import {
    ApiFunction,
    ApiListFunctions,
    ApiSubmittedFunctionCreation,
    ApiSubmittedFunctionUpdating
} from "./types";

import {defaultHeaders} from "./shared";

export interface ListFunctionsQuery {
    limit: number;
}

export async function listFunctions(query: ListFunctionsQuery): Promise<ApiListFunctions> {
    const response = await fetch("http://localhost:8008/v1/funcs", {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiListFunctions;
}

export interface SubmitCreateFunctionRequest {
    name: string;
}

export async function createFunction(req: SubmitCreateFunctionRequest): Promise<ApiSubmittedFunctionCreation> {
    const response = await fetch("http://localhost:8008/v1/funcs", {
        headers: defaultHeaders,
        method: "POST",
        body: JSON.stringify({
                name: req.name,
                inputs: {},
                code: {"value": ""}
            }
        )
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiSubmittedFunctionCreation;
}


export interface SubmitUpdateFunctionRequest {
    name: string;
    code: string;
}

export async function updateFunction(funcId: string, req: SubmitUpdateFunctionRequest): Promise<ApiSubmittedFunctionUpdating> {
    const response = await fetch(`http://localhost:8008/v1/funcs/${funcId}`, {
        headers: defaultHeaders,
        method: "PUT",
        body: JSON.stringify({
                name: req.name,
                inputs: {},
                code: {"value": req.code}
            }
        )
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiSubmittedFunctionUpdating;
}

export async function getFunction(id: string): Promise<ApiFunction> {
    const response = await fetch(`http://localhost:8008/v1/funcs/${id}`, {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiFunction;
}