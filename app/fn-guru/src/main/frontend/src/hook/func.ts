import {useCallback} from "react";
import {useGetAction} from "@/hook/http.ts";
import {Func, FuncList} from "@/types/func.ts";

type FuncGetAction = (funcId: string, abortController?: AbortController) => void
export const useFuncGet = (): [FuncGetAction, Func, boolean, Error] => {
    const [get, func, loading, error] = useGetAction<Func>()
    const fn = useCallback(async (funcId: string, abortController?: AbortController) => get(`/v1/funcs/${funcId}`, abortController), [])
    return [fn, func, loading, error]
}

type FuncListAction = (flowId: string, abortController?: AbortController) => void
export const useFuncList = (): [FuncListAction, FuncList, boolean, Error] => {
    const [get, func, loading, error] = useGetAction<FuncList>()
    const fn = useCallback(async (flowId: string, abortController?: AbortController) => get(`/v1/flows/${flowId}/funcs`, abortController), [])
    return [fn, func, loading, error]
}
