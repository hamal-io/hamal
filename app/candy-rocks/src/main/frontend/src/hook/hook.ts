import {FuncCreateRequested, HookCreateRequested, HookList} from "@/types";
import {useAuth} from "@/hook/auth.ts";
import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";

type HookListAction = (namespaceId: string, abortController?: AbortController) => void
export const useHookList = (): [HookListAction, HookList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, hookList, loading, error] = useGet<HookList>()
    const fn = useCallback(async (namespaceId: string, abortController?: AbortController) => get(`/v1/namespaces/${namespaceId}/hooks`, abortController), [auth])
    return [fn, hookList, loading, error]
}

type HookCreateAction = (namespaceId: string, name: string, abortController?: AbortController) => void
export const useHookCreate = (): [HookCreateAction, HookCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<HookCreateRequested>()
    const fn = useCallback(async (namespaceId: string, name: string, abortController?: AbortController) =>
        post(`/v1/namespaces/${namespaceId}/hooks`, {
            name,
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}
