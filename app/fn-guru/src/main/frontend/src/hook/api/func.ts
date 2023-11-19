import {useAuth} from "@/hook/auth.ts";
import {useCallback, useState} from "react";
import {AUTH_KEY} from "@/types/auth.ts";

interface ApiFuncCreateSubmitted {
    id: string;
    status: string;
    funcId: string;
    flowId: string;
    groupId: string;
}

type ApiFuncCreateAction = (flowId: string, name: string, controller?: AbortController) => void
export const useApiFuncCreate = (): [ApiFuncCreateAction, ApiFuncCreateSubmitted, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<ApiFuncCreateSubmitted | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const post = useCallback(async (flowId: string, name: string, abortController?: AbortController) => {
        if (auth.type === 'Unauthorized') {
            console.log("Unauthorized")
            setError(Error("Unauthenticated"))
            setIsLoading(false)
            setAuth(null)
            window.location.href = '/'

        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/v1/flows/${flowId}/funcs`, {
                method: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify({
                    name,
                    inputs: {},
                    code: ""
                }),
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

type ApiDeployLatestCodeAction = (funcId: string, abortController?: AbortController) => void
export const useApiDeployLatestCode = <T>(): [ApiDeployLatestCodeAction, T, boolean, Error] => {
    const [auth] = useAuth()

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const fn = useCallback(async (funcId: string, abortController?: AbortController) => {
            if (auth.type === 'Unauthorized') {
                console.log("Unauthorized")
                setError(Error("Unauthenticated"))
                setIsLoading(false)

                localStorage.removeItem(AUTH_KEY)
                window.location.href = '/'

            } else {
                fetch(`${import.meta.env.VITE_BASE_URL}/v1/funcs/${funcId}/deploy/latest`, {
                    method: "POST",
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
                            localStorage.removeItem(AUTH_KEY)
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
        }, [auth]
    )

    return [fn, data, isLoading, error]
}

interface ApiFuncInvokeSubmitted {
    id: string;
    status: string;
    funcId: string;
}

type ApiFuncInvokeAction = (funcId: string, controller?: AbortController) => void
export const useApiFuncInvoke = (): [ApiFuncInvokeAction, ApiFuncInvokeSubmitted, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<ApiFuncInvokeSubmitted | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const post = useCallback(async (funcId: string, abortController?: AbortController) => {
        if (auth.type === 'Unauthorized') {
            console.log("Unauthorized")
            setError(Error("Unauthenticated"))
            setIsLoading(false)
            setAuth(null)
            window.location.href = '/'

        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/v1/funcs/${funcId}/invoke`, {
                method: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify({
                    inputs: {},
                    events: []
                }),
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