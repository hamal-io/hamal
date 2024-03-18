import {useGet, usePatch, usePost} from "@/hook/http.ts";
import {useCallback, useEffect, useState} from "react";
import {useAuth} from "@/hook/auth.ts";
import {Namespace, NamespaceAppendRequested, NamespaceList, NamespaceUpdateRequested} from "@/types";

type NamespaceGetAction = (namespaceId: string, abortController?: AbortController) => void
export const useNamespaceGet = (): [NamespaceGetAction, Namespace, boolean, Error] => {
    const [auth] = useAuth()
    const [get, apiNamespace, loading, error] = useGet<ApiNamespace>()
    const fn = useCallback(async (namespaceIdId: string, abortController?: AbortController) => get(`/v1/namespaces/${namespaceIdId}`, abortController), [auth])
    const [namespace, setNamespace] = useState<Namespace>(null)

    useEffect(() => {
        if (apiNamespace) {
            setNamespace(() => parseNamespace(apiNamespace))
        }
    }, [apiNamespace]);

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

type NamespaceUpdateAction = (namespaceId: string, name: string, _features: string[], abortController?: AbortController) => void
export const useNamespaceUpdate = (): [NamespaceUpdateAction, NamespaceUpdateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [patch, submission, loading, error] = usePatch<NamespaceUpdateRequested>()
    const fn = useCallback(async (namespaceId: string, name: string, _features: string[], abortController?: AbortController) => {
            const features = toApiFeature(_features)
            patch(`/v1/namespaces/${namespaceId}`, {name, features}, abortController)
        }, [auth]
    )
    return [fn, submission, loading, error]
}

function toApiFeature(active: string[]) {
    const res = {}
    active.forEach(n =>
        res[n] = 0
    )
    return res
}

function parseNamespace(apiNamespace: ApiNamespace) {
    const features = ""
    for (const [k, v] of Object.entries(apiNamespace.features)) {
        features.concat(k + ",")
    }
    const res: Namespace = {
        name: apiNamespace.name,
        id: apiNamespace.id,
        features: features
    }

    return res
}

interface ApiNamespace {
    id: string;
    name: string;
    features: { [key: string]: number };
}





