import {
    ApiExecution,
    ApiListExecutions,
    ApiListTriggers,
    ApiSubmittedAdhocInvocation,
    ApiSubmittedTriggerCreation,
    ApiTrigger,
    TriggerType
} from "./types";


import {defaultHeaders} from "./shared";

export interface SubmitAdhocInvocationRequest {
    code: string;
}

export async function invokeAdhoc(req: SubmitAdhocInvocationRequest): Promise<ApiSubmittedAdhocInvocation> {
    //const response = await fetch("http://localhost:8008/v1/adhoc",
    //new
    const response = await fetch("http://localhost:8008/v1/groups/1/adhoc", {
        //end new
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

export interface ListExecutionsQuery {
    limit: number;
}

export async function listExecutions(query: ListExecutionsQuery): Promise<ApiListExecutions> {
    const response = await fetch("http://localhost:8008/v1/execs", {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiListExecutions;
}


export async function getExecution(id: string): Promise<ApiExecution> {
    const response = await fetch(`http://localhost:8008/v1/execs/${id}`, {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiExecution;
}


export interface ListTriggersQuery {
    limit: number;
}

export async function listTriggers(query: ListTriggersQuery): Promise<ApiListTriggers> {
    const response = await fetch("http://localhost:8008/v1/triggers", {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiListTriggers;
}

export interface SubmitCreateTriggerRequest {
    name: string;
    type: TriggerType;
    id: string;
    duration?: string;
    topicId?: string;
}

export async function createTrigger(req: SubmitCreateTriggerRequest): Promise<ApiSubmittedTriggerCreation> {
    const response = await fetch("http://localhost:8008/v1/triggers", {
        headers: defaultHeaders,
        method: "POST",
        body: JSON.stringify({
                name: req.name,
                type: req.type,
                inputs: {},
                funcId: req.id,
                duration: req.duration,
                topicId: req.topicId
            }
        )
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiSubmittedTriggerCreation;
}


export async function getTrigger(id: string): Promise<ApiTrigger> {
    const response = await fetch(`http://localhost:8008/v1/triggers/${id}`, {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiTrigger;
}

export * from './func';
export * from './topic';