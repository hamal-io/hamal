import {useAuth} from "@/hook/auth.ts";
import {useCallback, useEffect, useState} from "react";
import {ApiFuncSimple, VersionedCode} from "@/api/types";
import {AUTH_KEY} from "@/types/auth.ts";

export interface Func {
    id: string;
    name: string;
    inputs: object;
    code: {
        id: string;
        current: VersionedCode;
        deployed: VersionedCode;
    }
}

export const useFuncGet = (funcId: string): [Func | null, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [func, setFunc] = useState<Func | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    useEffect(() => {
        const abortController = new AbortController();

        if (auth.type === 'Unauthorized') {
            setError(Error("Unauthenticated"))
            setIsLoading(false)
        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/v1/funcs/${funcId}`, {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                method: "GET",
                signal: abortController.signal
            })
                .then(response => {

                    console.log("response", JSON.stringify(response))
                    if (response.status === 403) {
                        console.log("forbidden")
                        setAuth(null)
                        window.location.href = '/login'
                    }

                    if (!response.ok) {
                        setError(Error(`Request submission failed: ${response.status} - ${response.statusText}`))
                        setIsLoading(false)
                    }


                    response.json().then(data => {
                        setFunc(data)
                        setIsLoading(false)
                    })
                })
                .catch(error => {
                    if (error.name !== 'AbortError') {
                        // FIXME NETWORK ERROR
                        setError(error)
                        setIsLoading(false)
                    }


                    if (error.message === 'NetworkError when attempting to fetch resource.') {
                        console.log("forbidden")
                        setAuth(null)
                        window.location.href = '/login'
                    }
                })
        }
        return () => {
            abortController.abort();
        };
    }, []);

    return [func, isLoading, error]
}


export const useApiFuncList = (flowId: string): [ApiFuncSimple[], boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [funcs, setFuncs] = useState<ApiFuncSimple[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    useEffect(() => {
        const abortController = new AbortController();

        if (auth.type === 'Unauthorized') {
            setError(Error("Unauthenticated"))
            setIsLoading(false)
        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/v1/flows/${flowId}/funcs`, {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                method: "GET",
                signal: abortController.signal
            })
                .then(response => {

                    console.log("response", JSON.stringify(response))
                    if (response.status === 403) {
                        console.log("forbidden")
                        setAuth(null)
                        window.location.href = '/login'
                    }

                    if (!response.ok) {
                        setError(Error(`Request submission failed: ${response.status} - ${response.statusText}`))
                        setIsLoading(false)
                    }


                    response.json().then(data => {
                        setFuncs(data.funcs.filter(func => func.name !== '__default__'))
                        setIsLoading(false)
                    })
                })
                .catch(error => {
                    if (error.name !== 'AbortError') {
                        // FIXME NETWORK ERROR
                        setError(error)
                        setIsLoading(false)
                    }


                    if (error.message === 'NetworkError when attempting to fetch resource.') {
                        console.log("forbidden")
                        setAuth(null)
                        window.location.href = '/login'
                    }
                })
        }
        return () => {
            abortController.abort();
        };
    }, []);

    return [funcs, isLoading, error]
}


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