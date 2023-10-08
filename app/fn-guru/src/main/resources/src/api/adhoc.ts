import {ApiSubmittedReqWithId} from "./types";

export const defaultHeaders = {
    'Accept': 'application/json',
    'Content-Type': 'application/json'
}

export interface ApiSubmitAdhocReq {
    code: string;
}

export async function invokeAdhoc(req: ApiSubmitAdhocReq): Promise<ApiSubmittedReqWithId> {
    //FIXME do not use admin endpoint - only for prototyping
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/groups/1/adhoc`, {
        headers: defaultHeaders,
        method: "POST",
        body: JSON.stringify({
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