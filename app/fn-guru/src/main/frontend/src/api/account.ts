import {useEffect, useState} from "react";
import {useAuth} from "@/hook/auth.ts";

const unauthorizedDefaultHeaders = () => ({
    'Accept': 'application/json',
    'Content-Type': 'application/json',
})

export interface ApiLoginSubmitted {
    id: string;
    status: string;
    accountId: string;
    groupIds: string[];
    token: string;
    name: string;
}


export interface ApiAccountConversionSubmitted {
    id: string;
    status: string;
    accountId: string;
    token: string;
    name: string;
}

export const useCreateAnonymousAccount = <T>(): [T, boolean, Error] => {
    const [, setAuth] = useAuth()

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    useEffect(() => {
        const abortController = new AbortController();

        fetch(`${import.meta.env.VITE_BASE_URL}/v1/anonymous-accounts`, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            method: "POST",
            signal: abortController.signal
        })
            .then(response => {
                if (!response.ok) {
                    setError(Error(`Request submission failed: ${response.status} - ${response.statusText}`))
                    setIsLoading(false)
                }

                response.json().then(data => {
                    setData(data)
                    setIsLoading(false)

                    setAuth({
                        type: 'Anonymous',
                        accountId: data.accountId,
                        groupId: data.groupIds[0],
                        defaultFlowIds: data.defaultFlowIds,
                        token: data.token,
                        name: data.name
                    })
                })
            })
            .catch(error => {
                if (error.name !== 'AbortError') {
                    // FIXME NETWORK ERROR
                    setError(error)
                    setIsLoading(false)
                }
            })

        return () => {
            abortController.abort();
        };
    }, []);

    return [data, isLoading, error]
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