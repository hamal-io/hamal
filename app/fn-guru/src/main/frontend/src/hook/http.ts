import {useAuth} from "@/hook/auth.ts";
import {useCallback, useState} from "react";

type GetAction = (url: string, abortController?: AbortController) => void
export const useGetAction = <T>(): [GetAction, T, boolean, Error] => {
    const [auth, setAuth] = useAuth()
    const [data, setData] = useState<T | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    const fn = useCallback(async (url: string, abortController?: AbortController) => {
        setLoading(true);
        setError(null);

        if (auth.type === 'Unauthorized') {
            setError(Error("Unauthenticated"))
            setLoading(false)
            setAuth(null)
            window.location.href = '/'
        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}${url}`, {
                method: "GET",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                signal: abortController?.signal
            })
                .then(response => {
                    if (response.status === 403) {
                        console.log("forbidden")
                        setAuth(null)
                        window.location.href = '/'
                    }

                    if (!response.ok) {
                        setError(Error(`Request submission failed: ${response.status} - ${response.statusText}`))
                        setLoading(false)
                    }
                    response.json().then(data => {
                        setData(data)
                        setLoading(false)
                    })
                })
                .catch(error => {
                    if (error.name !== 'AbortError') {
                        // FIXME NETWORK ERROR
                        setError(error)
                        setLoading(false)
                    }

                    if (error.message === 'NetworkError when attempting to fetch resource.') {
                        console.log("forbidden")
                        setAuth(null)
                        window.location.href = '/login'
                    }
                })
        }
    }, [auth, error])

    return [fn, data, loading, error]
}
