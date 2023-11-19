import {Auth, AUTH_KEY} from "@/types/auth.ts";
import useLocalStorageState from "use-local-storage-state";
import {usePost} from "@/hook/http.ts";
import {FuncCreateSubmitted} from "@/types";
import {useCallback} from "react";

export const unauthorized: Auth = {
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

type FuncCreateAction = (flowId: string, name: string, abortController?: AbortController) => void
export const useFuncCreate = (): [FuncCreateAction, FuncCreateSubmitted, boolean, Error] => {
    const [post, submission, loading, error] = usePost<FuncCreateSubmitted>()
    const fn = useCallback(async (flowId: string, name: string, abortController?: AbortController) =>
        post(`/v1/flows/${flowId}/funcs`, {
            name,
            inputs: {},
            code: ""
        }, abortController), []
    )
    return [fn, submission, loading, error]
}

type LogoutAction = (abortController?: AbortController) => void
export const useLogout = (): [LogoutAction, never, boolean, Error] => {
    const [, setAuth] = useAuth()
    const [post, submission, loading, error] = usePost<never>()
    const fn = useCallback(async (abortController?: AbortController) => {
        post('/v1/logout', {}, abortController)
        setAuth({...unauthorized})
    }, [])
    return [fn, submission, loading, error]
}