import {Auth, AUTH_KEY} from "@/types/auth.ts";
import useLocalStorageState from "use-local-storage-state";
import {useCallback, useState} from "react";
import {useNavigate} from "react-router-dom";
import {DefaultFlowId} from "@/types";

const unauthorized: Auth = {
    type: 'Unauthorized',
    accountId: '',
    groupId: '',
    defaultFlowIds: Array<DefaultFlowId>(),
    token: ''
}

export const useAuth = () => {
    return useLocalStorageState<Auth>(AUTH_KEY, {
        defaultValue: {...unauthorized}
    })
}

type LogoutAction = (abortController?: AbortController) => void
export const useLogout = (): [LogoutAction, boolean, Error] => {
    const navigate = useNavigate()
    const [auth, setAuth] = useAuth()
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    const fn = useCallback((abortController?: AbortController) => {
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
                setAuth({...unauthorized})
                navigate("/", {replace: true})
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
                        groupId: data.groupIds[0],
                        defaultFlowIds: data.defaultFlowIds,
                        token: data.token,
                    })

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