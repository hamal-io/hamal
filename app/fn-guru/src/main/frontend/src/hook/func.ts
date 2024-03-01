import {useCallback} from "react";
import {useGet, usePatch, usePost} from "@/hook/http.ts";
import {
    Func,
    FuncCreateRequested,
    FuncDeploymentList,
    FuncDeployRequested,
    FuncInvokeRequested,
    FuncList,
    FuncUpdateRequested
} from "@/types";
import {useAuth} from "@/hook/auth.ts";

type FuncGetAction = (funcId: string, abortController?: AbortController) => void
export const useFuncGet = (): [FuncGetAction, Func, boolean, Error] => {
    const [auth] = useAuth()
    const [get, func, loading, error] = useGet<Func>()
    const fn = useCallback(async (funcId: string, abortController?: AbortController) => get(`/v1/funcs/${funcId}`, abortController), [auth])
    return [fn, func, loading, error]
}

type FuncListAction = (namespaceId: string, abortController?: AbortController) => void
export const useFuncList = (): [FuncListAction, FuncList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, funcList, loading, error] = useGet<FuncList>()
    const fn = useCallback(async (namespaceId: string, abortController?: AbortController) => get(`/v1/namespaces/${namespaceId}/funcs`, abortController), [auth])
    return [fn, funcList, loading, error]
}

type FuncCreateAction = (namespaceId: string, name: string, abortController?: AbortController) => void
export const useFuncCreate = (): [FuncCreateAction, FuncCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<FuncCreateRequested>()
    const fn = useCallback(async (namespaceId: string, name: string, abortController?: AbortController) =>
        post(`/v1/namespaces/${namespaceId}/funcs`, {
            name,
            inputs: {},
            code: ""
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}

type FuncUpdateAction = (funcId: string, name?: string, code?: string, abortController?: AbortController) => void
export const useFuncUpdate = (): [FuncUpdateAction, FuncUpdateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [patch, submission, loading, error] = usePatch<FuncUpdateRequested>()
    const fn = useCallback(async (funcId: string, name?: string, code?: string, abortController?: AbortController) =>
        patch(`/v1/funcs/${funcId}`, {
            name,
            inputs: {},
            code
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}


type FuncInvokeAction = (funcId: string, controller?: AbortController) => void
export const useFuncInvoke = (): [FuncInvokeAction, FuncInvokeRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<FuncCreateRequested>()
    const fn = useCallback(async (funcId: string, abortController?: AbortController) =>
        post(`/v1/funcs/${funcId}/invoke`, {
            inputs: {},
            events: [],
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}

type FuncDeployLatestCodeAction = (funcId: string, message: string, abortController?: AbortController) => void
export const useFuncDeployLatestCode = (): [FuncDeployLatestCodeAction, FuncDeployRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<FuncDeployRequested>()
    const fn = useCallback(async (funcId: string, message: string, abortController?: AbortController) =>
        post(`/v1/funcs/${funcId}/deploy`, {
            message: message
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}

type FuncDeploymentsListAction = (funcId: string, abortController?: AbortController) => void
export const useFuncHistory = (): [FuncDeploymentsListAction, FuncDeploymentList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, funcDeploymentsList, loading, error] = useGet<FuncDeploymentList>()
    const fn = useCallback(async (funcId: string, abortController?: AbortController) =>
        get(`/v1/funcs/${funcId}/deployments`, abortController), [auth])
    return [fn, funcDeploymentsList, loading, error]
}