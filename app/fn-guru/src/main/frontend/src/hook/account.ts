import {useAuth} from "@/hook/auth.ts";
import {useCallback, useState} from "react";
import {AccountConvertRequested, FuncCreateRequested, LoginRequested} from "@/types";
import {useInitUiState, useUiState} from "@/hook/ui-state.ts";

type AccountCreateAnonymousAction = (abortController?: AbortController) => void
export const useAccountCreateAnonymous = (): [AccountCreateAnonymousAction, LoginRequested, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<LoginRequested | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    const fn = useCallback(async (abortController?: AbortController) => {
        fetch(`${import.meta.env.VITE_BASE_URL}/v1/anonymous-accounts`, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            signal: abortController.signal
        })
            .then(response => {
                if (response.status === 403) {
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

                    setAuth({
                        type: 'Anonymous',
                        accountId: data.accountId,
                        workspaceId: data.workspaceIds[0],
                        token: data.token,
                    })
                })
            })
            .catch(error => {
                if (error.name !== 'AbortError') {
                    // FIXME NETWORK ERROR
                    setError(error)
                    setLoading(false)
                }

                if (error.message === 'NetworkError when attempting to fetch resource.') {
                    setAuth(null)
                    window.location.href = '/login'
                }
            })
    }, [])

    return [fn, data, loading, error]
}

type AccountLoginAction = (email: string, password: string, controller?: AbortController) => void
export const useAccountLogin = (): [AccountLoginAction, LoginRequested, boolean, Error] => {
    const [auth, setAuth] = useAuth()
    const [initUiState] = useInitUiState()

    const [data, setData] = useState<LoginRequested | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const fn = useCallback((email: string, password: string, controller?: AbortController) => {
        fetch(`${import.meta.env.VITE_BASE_URL}/v1/login`, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            signal: controller?.signal,
            body: JSON.stringify({
                email,
                password
            })
        })
            .then(response => {
                if (!response.ok) {
                    setError(Error(`Request submission failed: ${response.status} - ${response.statusText}`))
                    setLoading(false)
                }
                response.json().then(data => {
                    setData(data)
                    setLoading(false)

                    setAuth({
                        type: 'User',
                        accountId: data.accountId,
                        workspaceId: data.workspaceIds[0],
                        token: data.token,
                    })

                    initUiState(data.workspaceIds[0], data.workspaceIds[0],'')
                })
            })
            .catch(error => {
                if (error.name !== 'AbortError') {
                    // FIXME NETWORK ERROR
                    setError(error)
                    setLoading(false)
                }

                if (error.message === 'NetworkError when attempting to fetch resource.') {
                    setAuth(null)
                    window.location.href = '/login'
                }
            })
    }, [])

    return [fn, data, loading, error]
}


type AccountConvertAction = (email: string, password: string, abortController?: AbortController) => void
export const useAccountConvert = (): [AccountConvertAction, AccountConvertRequested, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<AccountConvertRequested | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const fn = useCallback((email: string, password: string, abortController?: AbortController) => {
        fetch(`${import.meta.env.VITE_BASE_URL}/v1/anonymous-accounts/convert`, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${auth.token}`
            },
            signal: abortController?.signal,
            body: JSON.stringify({
                email,
                password
            })
        })
            .then(response => {
                if (!response.ok) {
                    setError(Error(`Request submission failed: ${response.status} - ${response.statusText}`))
                    setLoading(false)
                }
                response.json().then(data => {
                    setData(data)
                    setLoading(false)

                    setAuth({
                        type: 'User',
                        accountId: auth.accountId,
                        workspaceId: auth.workspaceId,
                        token: data.token,
                    })
                })
            })
            .catch(error => {
                if (error.name !== 'AbortError') {
                    // FIXME NETWORK ERROR
                    setError(error)
                    setLoading(false)
                }

                if (error.message === 'NetworkError when attempting to fetch resource.') {
                    setAuth(null)
                    window.location.href = '/login'
                }
            })
    }, [auth])

    return [fn, data, loading, error]
}