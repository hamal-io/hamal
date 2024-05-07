import {useCallback, useState} from "react";
import {useAuth, useResetAuth} from "@/hook/auth.ts";

type GetAction = (url: string, abortController?: AbortController) => void
export const useGet = <T>(): [GetAction, T, boolean, Error] => {
    const [auth] = useAuth()
    const resetAuth = useResetAuth()
    const [data, setData] = useState<T | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<Error | null>(null);

    const fn = useCallback(async (url: string, abortController?: AbortController) => {
        if (auth.type === 'Unauthorized') {
            setError(Error("Unauthenticated"))
            setLoading(false)
            resetAuth()
            window.location.href = '/'
        } else {
            setLoading(true)
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
                        resetAuth()
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
                        resetAuth()
                        window.location.href = '/'
                    }
                })
        }
    }, [auth])

    return [fn, data, loading, error]
}

type PostAction = (url: string, data: object, abortController?: AbortController) => void
export const usePost = <T>(): [PostAction, T, boolean, Error] => {
    const [auth] = useAuth()
    const resetAuth = useResetAuth()
    const [data, setData] = useState<T | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<Error | null>(null);

    const fn = useCallback(async (url: string, body: object, abortController?: AbortController) => {
        if (auth.type === 'Unauthorized') {
            setError(Error("Unauthenticated"))
            setLoading(false)
            resetAuth()
            window.location.href = '/'

        } else {
            setLoading(true)
            fetch(`${import.meta.env.VITE_BASE_URL}${url}`, {
                method: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify(body),
                signal: abortController?.signal
            })
                .then(response => {

                    if (response.status === 403) {
                        resetAuth()
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
                        resetAuth()
                        window.location.href = '/'
                    }

                })
        }
    }, [auth])

    return [fn, data, loading, error]
}


type PatchAction = (url: string, data: object, abortController?: AbortController) => void
export const usePatch = <T>(): [PatchAction, T, boolean, Error] => {
    const [auth] = useAuth()
    const resetAuth = useResetAuth()

    const [data, setData] = useState<T | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<Error | null>(null);

    const fn = useCallback(async (url: string, body: object, abortController?: AbortController) => {
        if (auth.type === 'Unauthorized') {
            setError(Error("Unauthenticated"))
            setLoading(false)
            resetAuth()
            window.location.href = '/'

        } else {
            setLoading(true)
            fetch(`${import.meta.env.VITE_BASE_URL}${url}`, {
                method: "PATCH",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify(body),
                signal: abortController?.signal
            })
                .then(response => {

                    if (response.status === 403) {
                        resetAuth()
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
                        resetAuth()
                        window.location.href = '/'
                    }

                })
        }
    }, [auth])

    return [fn, data, loading, error]
}
