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

type NamespaceListAction = (groupId: string, abortController?: AbortController) => void
export const useNamespaceList = (): [NamespaceListAction, NamespaceList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, namespaceList, loading, error] = useGet<NamespaceList>()
    const fn = useCallback(async (groupId: string, abortController?: AbortController) => get(`/v1/groups/${groupId}/namespaces`, abortController), [auth])
    return [fn, namespaceList, loading, error]
}

type NamespaceCreateAction = (groupId: string, name: string, controller?: AbortController) => void
export const useNamespaceCreate = (): [NamespaceCreateAction, NamespaceCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<NamespaceCreateRequested>()
    const fn = useCallback(async (groupId: string, name: string, abortController?: AbortController) =>
        post(`/v1/groups/${groupId}/namespaces`, {
            name,
            inputs: {}
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}
