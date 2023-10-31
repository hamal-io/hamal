const unauthorizedDefaultHeaders = () => ({
    'Accept': 'application/json',
    'Content-Type': 'application/json',
})

export interface ApiLoginSubmitted {
    id: string,
    status: string,
    accountId: string,
    groupIds: string[];
    token: string
}


export async function login(username: string, password: string): Promise<ApiLoginSubmitted> {
    //FIXME do not use admin endpoint - only for prototyping
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}/v1/login`, {
        headers: unauthorizedDefaultHeaders(),
        method: "POST",
        body: JSON.stringify({
            username,
            password
        })
    })

    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiLoginSubmitted;
}