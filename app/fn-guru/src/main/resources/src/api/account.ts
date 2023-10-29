import {unauthorizedDefaultHeaders} from "./shared.ts";

interface ApiAccountCreateSubmitted {
    id: string,
    status: string,
    token: string
}

export async function createAnonymousAccount(): Promise<ApiAccountCreateSubmitted> {
    //FIXME do not use admin endpoint - only for prototyping
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/anonymous-accounts`, {
        headers: unauthorizedDefaultHeaders(),
        method: "POST",
    })

    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json();
}