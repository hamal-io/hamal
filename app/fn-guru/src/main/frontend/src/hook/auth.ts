import {Auth, AUTH_KEY} from "@/types/auth.ts";
import useLocalStorageState from "use-local-storage-state";
import {usePost} from "@/hook/http.ts";
import {useCallback, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";

const unauthorized: Auth = {
    type: 'Unauthorized',
    accountId: '',
    groupId: '',
    defaultFlowIds: new Map<string, string>(),
    token: '',
    name: ''
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