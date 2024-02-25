import {useGet, usePatch, usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {useAuth} from "@/hook/auth.ts";
import {Namespace, NamespaceAppendRequested, NamespaceList, NamespaceUpdateRequested, NamespaceView} from "@/types";

type NamespaceGetAction = (namespaceIdId: string, abortController?: AbortController) => void
export const useNamespaceGet = (): [NamespaceGetAction, Namespace, boolean, Error] => {
    const [auth] = useAuth()
    const [get, namespace, loading, error] = useGet<Namespace>()
    const fn = useCallback(async (namespaceIdId: string, abortController?: AbortController) => get(`/v1/namespaces/${namespaceIdId}`, abortController), [auth])
    return [fn, namespace, loading, error]
}

type NamespaceListAction = (workspaceId: string, abortController?: AbortController) => void
export const useNamespaceList = (): [NamespaceListAction, NamespaceList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, namespaceList, loading, error] = useGet<NamespaceList>()
    const fn = useCallback(async (workspaceId: string, abortController?: AbortController) => get(`/v1/workspaces/${workspaceId}/namespaces`, abortController), [auth])
    return [fn, namespaceList, loading, error]
}

type NamespaceAppendAction = (namespaceId: string, name: string, abortController?: AbortController) => void
export const useNamespaceAppend = (): [NamespaceAppendAction, NamespaceAppendRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<NamespaceAppendRequested>()
    const fn = useCallback(async (namespaceId: string, name: string, abortController?: AbortController) =>
        post(`/v1/namespaces/${namespaceId}/namespaces`, {name}, abortController), [auth]
    )
    return [fn, submission, loading, error]
}

type NamespaceUpdateAction = (namespaceId: string, name: string, abortController?: AbortController) => void
export const useNamespaceUpdate = (): [NamespaceUpdateAction, NamespaceUpdateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [patch, submission, loading, error] = usePatch<NamespaceAppendRequested>()
    const fn = useCallback(async (namespaceId: string, name: string, abortController?: AbortController) =>
        patch(`/v1/namespaces/${namespaceId}`, {name}, abortController), [auth]
    )
    return [fn, submission, loading, error]
}