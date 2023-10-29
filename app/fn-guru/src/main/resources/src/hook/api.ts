import {useEffect, useState} from "react";
import useLocalStorageState from "use-local-storage-state";
import {AUTH_STATE_NAME, AuthState} from "../state.ts";

export interface UseApiProps {
    method: string,
    url: string,
    body?: string
}


export const useApi = <T>({method, url, body}: UseApiProps) => {
    const [auth] = useLocalStorageState<AuthState>(AUTH_STATE_NAME)

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    useEffect(() => {
        if (!auth) {
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
    }, []);

    return {data, isLoading, error}
}
