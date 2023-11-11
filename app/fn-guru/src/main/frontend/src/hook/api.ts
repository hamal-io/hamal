import {useCallback, useEffect, useState} from "react";
import {ApiLoginSubmitted} from "../api/account.ts";
import {useAuth} from "@/hook/auth.ts";
import {AUTH_KEY} from "@/types/auth.ts";

type ApiDeployLatestCodeAction = (funcId: string, abortController?: AbortController) => void
export const useApiDeployLatestCode = <T>(): [ApiDeployLatestCodeAction, T, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const fn = useCallback(async (funcId: string, abortController?: AbortController) => {
            if (auth.type === 'Unauthorized') {
                console.log("Unauthorized")
                setError(Error("Unauthenticated"))
                setIsLoading(false)

                setAuth(null)
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
        }, [auth]
    )

    return [fn, data, isLoading, error]
}

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
                setError(error)
                setIsLoading(false)
            })
    }, [auth])

    return [create, data, isLoading, error]
}

interface ApiFlowCreateSubmitted {
    id: string;
    status: string;
    flowId: string;
    groupId: string;
}

type ApiFlowCreateAction = (groupId: string, name: string, controller?: AbortController) => void
export const useApiFlowCreate = (): [ApiFlowCreateAction, ApiFlowCreateSubmitted, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<ApiFlowCreateSubmitted | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const post = useCallback(async (groupId: string, name: string, abortController?: AbortController) => {
        if (auth.type === 'Unauthorized') {
            console.log("Unauthorized")
            setError(Error("Unauthenticated"))
            setIsLoading(false)
            setAuth(null)
            window.location.href = '/'

        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/v1/groups/${groupId}/flows`, {
                method: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify({
                    name,
                    inputs: {}
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


type ApiLoginAccountAction = (username: string, password: string, controller?: AbortController) => void
export const useApiAccountLogin = (): [ApiLoginAccountAction, ApiLoginSubmitted, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<ApiLoginSubmitted | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const create = useCallback((username: string, password: string, controller?: AbortController) => {
        fetch(`${import.meta.env.VITE_BASE_URL}/v1/login`, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            signal: controller?.signal,
            body: JSON.stringify({
                username,
                password
            })
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
                        type: 'User',
                        accountId: data.accountId,
                        groupId: data.groupIds[0],
                        defaultFlowIds: data.defaultFlowIds,
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


export interface ApiLogoutSubmitted {
    id: string;
    status: string;
    accountId: string;
}


type ApiLogoutAccountAction = () => void
export const useApiAccountLogout = (): [ApiLogoutAccountAction, ApiLogoutSubmitted, boolean, Error] => {
    const [auth, setAuth] = useAuth()
    const [data, setData] = useState<ApiLogoutSubmitted | null>(null);
    const [error, setError] = useState<Error>(null);
    const [isLoading, setIsLoading] = useState(true);

    const post = useCallback(async () => {
        if (auth.type === 'Unauthorized') {
            console.log("Unauthorized")
            setError(Error("Unauthenticated"))
            setIsLoading(false)
            setAuth(null)
            window.location.href = '/'
        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/v1/logout`, {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                method: "POST",
                body: JSON.stringify(auth.accountId)
            }).then(response => {
                if (!response.ok) {
                    const message = `Request submission failed: ${response.status} - ${response.statusText}`;
                    throw new Error(message);
                }

                response.json().then(data => {
                    setData(data)
                    setIsLoading(false)
                    setAuth(null)
                })
                localStorage.removeItem(AUTH_KEY)
                window.location.href = '/'
            }).catch(error => {
                setError(error)
                setIsLoading(false)
            })
        }
    }, [auth])
    return [post, data, isLoading, error]
}

export const useApiGet = <T>(url: string): [T, boolean, Error] => {
    const [auth, setAuth] = useAuth()

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

    return [data, isLoading, error]
}

type ApiPostAction = (url: string, data: object, abortController?: AbortController) => void
export const useApiPost = <T>(): [ApiPostAction, T, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const post = useCallback(async (url: string, body: object, abortController?: AbortController) => {
        if (auth.type === 'Unauthorized') {
            console.log("Unauthorized")
            setError(Error("Unauthenticated"))
            setIsLoading(false)
            setAuth(null)
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

type ApiPatchAction = (url: string, data: object, abortController?: AbortController) => void
export const useApiPatch = <T>(): [ApiPatchAction, T, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const patch = useCallback(async (url: string, body: object, abortController?: AbortController) => {
        if (auth.type === 'Unauthorized') {
            console.log("Unauthorized")
            setError(Error("Unauthenticated"))
            setIsLoading(false)

            setAuth(null)
            window.location.href = '/'

        } else {
            fetch(`${import.meta.env.VITE_BASE_URL}/${url}`, {
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

    return [patch, data, isLoading, error]
}

