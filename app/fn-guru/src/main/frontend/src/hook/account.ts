import {useAuth} from "@/hook/auth.ts";
import {useCallback, useState} from "react";
import {AccountConvertSubmitted, FuncCreateSubmitted, LoginSubmitted} from "@/types";
import {usePost} from "@/hook/http.ts";

type AccountCreateAnonymousAction = (abortController?: AbortController) => void
export const useAccountCreateAnonymous = (): [AccountCreateAnonymousAction, LoginSubmitted, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<LoginSubmitted | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    const create = useCallback(async (abortController?: AbortController) => {
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

type AccountLoginAction = (name: string, password: string, controller?: AbortController) => void
export const useAccountLogin = (): [AccountLoginAction, LoginSubmitted, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<LoginSubmitted | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const create = useCallback((name: string, password: string, controller?: AbortController) => {
        fetch(`${import.meta.env.VITE_BASE_URL}/v1/login`, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            signal: controller?.signal,
            body: JSON.stringify({
                name,
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


type AccountConvertAction = (name: string, password: string, email?: string, abortController?: AbortController) => void
export const useAccountConvert = (): [AccountConvertAction, AccountConvertSubmitted, boolean, Error] => {
    const [auth, setAuth] = useAuth()

    const [data, setData] = useState<AccountConvertSubmitted | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error>(null);

    const create = useCallback((name: string, password: string, email?: string, abortController?: AbortController) => {
        fetch(`${import.meta.env.VITE_BASE_URL}/v1/anonymous-accounts/convert`, {
            method: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${auth.token}`
            },
            signal: abortController?.signal,
            body: JSON.stringify({
                name,
                password,
                email
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
                        accountId: auth.accountId,
                        groupId: auth.groupId,
                        defaultFlowIds: auth.defaultFlowIds,
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