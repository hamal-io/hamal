import {FuncCreateSubmitted, HookCreateSubmitted, HookList} from "@/types";
import {useAuth} from "@/hook/auth.ts";
import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";

type HookListAction = (flowId: string, abortController?: AbortController) => void
export const useHookList = (): [HookListAction, HookList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, hookList, loading, error] = useGet<HookList>()
    const fn = useCallback(async (flowId: string, abortController?: AbortController) => get(`/v1/flows/${flowId}/hooks`, abortController), [auth])
    return [fn, hookList, loading, error]
}

type HookCreateAction = (flowId: string, name: string, abortController?: AbortController) => void
export const useHookCreate = (): [HookCreateAction, HookCreateSubmitted, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<HookCreateSubmitted>()
    const fn = useCallback(async (flowId: string, name: string, abortController?: AbortController) =>
        post(`/v1/flows/${flowId}/hooks`, {
            name,
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}
