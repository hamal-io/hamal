import {useEffect, useState} from "react";
import useLocalStorageState from "use-local-storage-state";
import {AUTH_STATE_NAME, AuthState} from "./state.ts";

export const useApi = <T>(url: string) => {
    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const [auth] = useLocalStorageState<AuthState | null>(AUTH_STATE_NAME, null)
    console.log(auth)
    // if (!response.ok) {
    //     const message = `Request submission failed: ${response.status} - ${response.statusText}`;
    //     throw new Error(message);
    // }


    useEffect(() => {
        setIsLoading(true)
        fetch(`${import.meta.env.VITE_BASE_URL}/v1/groups`, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${auth.token}`
            },
            method: "GET",
        })
            .then(response => {
                response.json().then(data => {
                    setData(data)
                    setIsLoading(false)
                })
            })
            .catch(error => {
                setError(error)
                setIsLoading(false)
            })

    }, []);

    return {data, isLoading, error}
}