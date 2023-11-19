import {useCallback} from "react";
import {useGet, usePatch, usePost} from "@/hook/http.ts";
import {Func, FuncCreateSubmitted, FuncDeployLatestSubmitted, FuncInvokeSubmitted, FuncList, FuncUpdateSubmitted} from "@/types";

type FuncGetAction = (funcId: string, abortController?: AbortController) => void
export const useFuncGet = (): [FuncGetAction, Func, boolean, Error] => {
    const [get, func, loading, error] = useGet<Func>()
    const fn = useCallback(async (funcId: string, abortController?: AbortController) => get(`/v1/funcs/${funcId}`, abortController), [])
    return [fn, func, loading, error]
}

type FuncListAction = (flowId: string, abortController?: AbortController) => void
export const useFuncList = (): [FuncListAction, FuncList, boolean, Error] => {
    const [get, funcList, loading, error] = useGet<FuncList>()
    const fn = useCallback(async (flowId: string, abortController?: AbortController) => get(`/v1/flows/${flowId}/funcs`, abortController), [])
    return [fn, funcList, loading, error]
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

type FuncUpdateAction = (funcId: string, name?: string, code?: string, abortController?: AbortController) => void
export const useFuncUpdate = (): [FuncUpdateAction, FuncUpdateSubmitted, boolean, Error] => {
    const [patch, submission, loading, error] = usePatch<FuncUpdateSubmitted>()
    const fn = useCallback(async (funcId: string, name?: string, code?: string, abortController?: AbortController) =>
        patch(`/v1/funcs/${funcId}`, {
            name,
            inputs: {},
            code
        }, abortController), []
    )
    return [fn, submission, loading, error]
}


type FuncInvokeAction = (funcId: string, controller?: AbortController) => void
export const useFuncInvoke = (): [FuncInvokeAction, FuncInvokeSubmitted, boolean, Error] => {
    const [post, submission, loading, error] = usePost<FuncCreateSubmitted>()
    const fn = useCallback(async (funcId: string, abortController?: AbortController) =>
        post(`/v1/funcs/${funcId}/invoke`, {
            inputs: {},
            events: [],
        }, abortController), []
    )
    return [fn, submission, loading, error]
}

type FuncDeployLatestCodeAction = (funcId: string, abortController?: AbortController) => void
export const useFuncDeployLatestCode = (): [FuncDeployLatestCodeAction, FuncDeployLatestSubmitted, boolean, Error] => {
    const [post, submission, loading, error] = usePost<FuncDeployLatestSubmitted>()
    const fn = useCallback(async (funcId: string, abortController?: AbortController) =>
        post(`/v1/funcs/${funcId}/deploy/latest`, {}, abortController), []
    )
    return [fn, submission, loading, error]
}