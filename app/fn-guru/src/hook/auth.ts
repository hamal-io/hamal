import {Auth, AUTH_KEY, UpdatePasswordRequested} from "../types/auth.ts";
import useLocalStorageState from "use-local-storage-state";
import {useCallback, useState} from "react";
import {useNavigate} from "react-router-dom";
import {useInitUiState, useResetUiState} from "./ui-state.ts";
import {usePatch} from "@/hook/http.ts";

const unauthorized: Auth = {
    type: 'Unauthorized',
    accountId: '',
    workspaceId: '',
    token: ''
}

export const useAuth = () => {
    return useLocalStorageState<Auth>(AUTH_KEY, {
        defaultValue: {...unauthorized}
    })
}

type ResetAuthAction = () => void
export const useResetAuth = (): [ResetAuthAction] => {
    const [_, setAuth] = useAuth()

    const fn = useCallback(() => {
        setAuth({...unauthorized})
    }, [setAuth])

    return [fn]
}


type LogoutAction = (abortController?: AbortController) => void
export const useLogout = (): [LogoutAction, boolean, Error] => {
    const navigate = useNavigate()
    const [auth] = useAuth()
    const [resetAuth] = useResetAuth()
    const [resetUiState] = useResetUiState()
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    const fn = useCallback((abortController?: AbortController) => {
        resetAuth()
        resetUiState()

        fetch(`${import.meta.env.VITE_BASE_URL}/v1/logout`, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${auth.token}`
            },
            signal: abortController?.signal,
        })
            .then(response => {
                setLoading(false)
                navigate("/", {replace: true})
            })
            .catch(error => {
                if (error.name !== 'AbortError') {
                    // FIXME NETWORK ERROR
                    setError(error)
                    setLoading(false)
                }

                if (error.message === 'NetworkError when attempting to fetch resource.') {
                    window.location.href = '/login'
                }
            })
    }, [auth])


    return [fn, loading, error]
}

type MetaMaskChallengeAction = (address: string, abortController?: AbortController) => void
export const useMetaMaskChallenge = (): [MetaMaskChallengeAction, string, boolean, Error] => {
    const [auth, setAuth] = useAuth()
    const [challenge, setChallenge] = useState<string | null>(null)
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    const fn = useCallback((address: string, abortController?: AbortController) => {
        fetch(`${import.meta.env.VITE_BASE_URL}/v1/metamask/challenge`, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({address}),
            signal: abortController?.signal,
        })
            .then(response => {
                response.json().then(data => {
                    setChallenge(data.challenge)
                })
                setLoading(false)
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

    return [fn, challenge, loading, error]
}

type MetaMaskTokenAction = (address: string, signature: string, abortController?: AbortController) => void
export const useMetaMaskToken = (): [MetaMaskTokenAction, string, boolean, Error] => {
    const [initUiState] = useInitUiState()
    const [auth, setAuth] = useAuth()
    const [token, setToken] = useState<string | null>(null)
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    const fn = useCallback((address: string, signature: string, abortController?: AbortController) => {
        fetch(`${import.meta.env.VITE_BASE_URL}/v1/metamask/token`, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                address,
                signature
            }),
            signal: abortController?.signal,
        })
            .then(response => {
                response.json().then(data => {
                    setToken(data.token)

                    setAuth({
                        type: 'User',
                        accountId: data.accountId,
                        workspaceId: data.workspaceIds[0],
                        token: data.token,
                    })

                    initUiState(data.workspaceIds[0], data.workspaceIds[0])

                })
                setLoading(false)
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

    return [fn, token, loading, error]
}

type UpdatePasswordAction = (
    currentPassword: string,
    newPassword: string,
    abortController?: AbortController
) => void
export const useUpdatePassword = (): [UpdatePasswordAction, UpdatePasswordRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [patch, submission, loading, error] = usePatch<UpdatePasswordRequested>()
    const fn = useCallback<UpdatePasswordAction>(async (currentPassword, newPassword, abortController?) =>
        patch(`/v1/auth`, {currentPassword, newPassword}, abortController), [auth]
    )
    return [fn, submission, loading, error]
}
