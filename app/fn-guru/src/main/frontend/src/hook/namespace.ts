import {useGet, usePatch, usePost} from "@/hook/http.ts";
import {useCallback, useEffect, useState} from "react";
import {useAuth} from "@/hook/auth.ts";
import {
    Feature,
    FeatureObject,
    features,
    Namespace,
    NamespaceAppendRequested,
    NamespaceList,
    NamespaceUpdateRequested
} from "@/types";
import feature from "@/pages/app/dashboard/components/feature.tsx";

interface ApiNamespace {
    id: string;
    name: string;
    features: FeatureObject;
}

type NamespaceGetAction = (namespaceId: string, abortController?: AbortController) => void
export const useNamespaceGet = (): [NamespaceGetAction, Namespace, boolean, Error] => {
    const [auth] = useAuth()
    const [get, apiNamespace, loading, error] = useGet<ApiNamespace>()
    const fn = useCallback(async (namespaceIdId: string, abortController?: AbortController) => get(`/v1/namespaces/${namespaceIdId}`, abortController), [auth])
    const [namespace, setNamespace] = useState<Namespace>(null)

    useEffect(() => {
        if (apiNamespace) {
            const copy = [...features]
            for (const [, v] of Object.entries(apiNamespace.features)) {
                const x = copy.find((c) => c.value === v)
                x.state = true
            }
            const res: Namespace = {
                name: apiNamespace.name,
                id: apiNamespace.id,
                features: copy
            }
            setNamespace(res)
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

type NamespaceUpdateAction = (namespaceId: string, name: string, features: Array<Feature>, abortController?: AbortController) => void
export const useNamespaceUpdate = (): [NamespaceUpdateAction, NamespaceUpdateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [patch, submission, loading, error] = usePatch<NamespaceUpdateRequested>()
    const fn = useCallback(async (namespaceId: string, name: string, features: Array<Feature>, abortController?: AbortController) => {
            const featureObject: FeatureObject = {}
            features.forEach((f) => {
                if (f.state === true) {
                    featureObject[f.name] = f.value
                }
            })
            patch(`/v1/namespaces/${namespaceId}`, {name, featureObject}, abortController)
        }, [auth]
    )
    return [fn, submission, loading, error]
}
