interface ApiSubmittedAdhocInvocation {
    reqId: string;
    status: string;
    id: string;
}

export const defaultHeaders = {
    'Accept': 'application/json',
    'Content-Type': 'application/json'
}

export interface SubmitAdhocInvocationRequest {
    code: string;
}

export async function invokeAdhoc(req: SubmitAdhocInvocationRequest): Promise<ApiSubmittedAdhocInvocation> {
    //FIXME do not use admin endpoint - only for prototyping
    const response = await fetch("http://localhost:9009/v1/adhoc", {
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