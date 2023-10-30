import {useCallback, useEffect, useState} from "react";
import useLocalStorageState from "use-local-storage-state";
import {AUTH_STATE_KEY, AuthState} from "../state.ts";

export interface UseApiProps {
    method: string,
    url: string,
    body?: string
}

export const useApi = <T>({method, url, body}: UseApiProps) => {
    const [auth] = useAuth()

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    useEffect(() => {
        if (auth.type === 'Unauthorized') {
            setError(Error("Unauthenticated"))
            setIsLoading(false)
        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/${url}`, {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                method,
                body
            })
                .then(response => {

                    if (response.status === 403) {
                        console.log("forbidden")
                        localStorage.removeItem(AUTH_STATE_KEY)
                        window.location.href = '/'
                    }

                    if (!response.ok) {
                        setError(Error(`Request submission failed: ${response.status} - ${response.statusText}`))
                        setIsLoading(false)
                    }


                    response.json().then(data => {
                        setData(data)
                        setIsLoading(false)
                    })
                })
                .catch(error => {
                    // FIXME NETWORK ERROR
                    setError(error)
                    setIsLoading(false)
                })
        }
    }, []);

    return {data, isLoading, error}
}

export const useApiPost = <T>() => {
    const [auth] = useAuth()

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const post = useCallback(async (url, body) => {
        if (auth.type === 'Unauthorized') {
            setError(Error("Unauthenticated"))
            setIsLoading(false)
        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/${url}`, {
                method: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify(body)
            })
                .then(response => {

                    if (response.status === 403) {
                        console.log("forbidden")
                        localStorage.removeItem(AUTH_STATE_KEY)
                        window.location.href = '/'
                    }


                    if (!response.ok) {
                        setError(Error(`Request submission failed: ${response.status} - ${response.statusText}`))
                        setIsLoading(false)
                    }
                    response.json().then(data => {
                        setData(data)
                        setIsLoading(false)
                    })
                })
                .catch(error => {
                    setError(error)
                    setIsLoading(false)
                })
        }
    }, [auth])

    return {post, data, isLoading, error}
}

export const useAuth = () => {
    return useLocalStorageState<AuthState>(AUTH_STATE_KEY, {
        defaultValue: {
            type: 'Unauthorized',
            accountId: '',
            groupId: '',
            token: ''
        }
    })
}