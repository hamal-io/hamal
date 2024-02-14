import {useGet, usePost} from "@/hook/http.ts";
import {useCallback, useState} from "react";
import {useAuth} from "@/hook/auth.ts";
import {Namespace, NamespaceCreateRequested, NamespaceList} from "@/types";

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

type NamespaceCreateAction = (workspaceId: string, name: string, controller?: AbortController) => void
export const useNamespaceCreate = (): [NamespaceCreateAction, NamespaceCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<NamespaceCreateRequested>()
    const fn = useCallback(async (workspaceId: string, name: string, abortController?: AbortController) =>
        post(`/v1/workspaces/${workspaceId}/namespaces`, {
            name,
            inputs: {}
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}
