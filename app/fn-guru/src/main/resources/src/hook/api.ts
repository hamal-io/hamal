import {useCallback, useEffect, useState} from "react";
import useLocalStorageState from "use-local-storage-state";
import {AUTH_STATE_KEY, AuthState} from "../state.ts";
import {ApiLoginSubmitted} from "../api/account.ts";

type ApiCreateAnonymousAccountAction = (controller: AbortController) => void

export const useApiCreateAnonymousAccount = (): [ApiCreateAnonymousAccountAction, ApiLoginSubmitted, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<ApiLoginSubmitted | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const create = useCallback(async (controller: AbortController) => {
        fetch(`${import.meta.env.VITE_BASE_URL}/v1/anonymous-accounts`, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            signal: controller.signal
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

                    setAuth({
                        type: 'Anonymous',
                        accountId: data.accountId,
                        groupId: data.groupIds[0],
                        token: data.token,
                        name: data.name
                    })
                })
            })
            .catch(error => {
                setError(error)
                setIsLoading(false)
            })
    }, [auth])

    return [create, data, isLoading, error]
}


export const useApiGet = <T>(url: string): [T, boolean, Error] => {
    const [auth] = useAuth()

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    useEffect(() => {
        const abortController = new AbortController();

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
                method: "GET",
                signal: abortController.signal
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
                    if (error.name !== 'AbortError') {
                        // FIXME NETWORK ERROR
                        setError(error)
                        setIsLoading(false)
                    }
                })
        }
        return () => {
            abortController.abort();
        };
    }, []);

    return [data, isLoading, error]
}

type ApiPostAction = (url: string, data: object, abortController?: AbortController) => void
export const useApiPost = <T>(): [ApiPostAction, T, boolean, Error] => {
    const [auth] = useAuth()

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const post = useCallback(async (url: string, body: object, abortController?: AbortController) => {
        if (auth.type === 'Unauthorized') {
            console.log("Unauthorized")
            setError(Error("Unauthenticated"))
            setIsLoading(false)

            localStorage.removeItem(AUTH_STATE_KEY)
            window.location.href = '/'

        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/${url}`, {
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

    return [post, data, isLoading, error]
}

export const useAuth = () => {
    return useLocalStorageState<AuthState>(AUTH_STATE_KEY, {
        defaultValue: {
            type: 'Unauthorized',
            accountId: '',
            groupId: '',
            token: '',
            name: ''
        }
    })
}