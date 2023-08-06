import {ApiListExecution, ApiSubmittedAdhocInvocation} from "./types";


const defaultHeaders = {
    'Accept': 'application/json',
    'Content-Type': 'application/json'
}


export interface SubmitAdhocInvocation {
    code: string;
}

export async function invokeAdhoc(req: SubmitAdhocInvocation): Promise<ApiSubmittedAdhocInvocation> {
    const response = await fetch("http://localhost:8008/v1/adhoc", {
        headers: defaultHeaders,
        method: "POST",
        body: JSON.stringify({
                inputs: {},
                code: {"value": req.code}
            }
        )
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiSubmittedAdhocInvocation;
}

export interface ListExecutionQuery {
    limit: number;
}

export async function listExecutions(query: ListExecutionQuery): Promise<ApiListExecution> {
    const response = await fetch("http://localhost:8008/v1/execs", {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiListExecution;
}