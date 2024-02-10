import {EndpointCreateSubmitted, EndpointList} from "@/types";
import {useAuth} from "@/hook/auth.ts";
import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";

type EndpointListAction = (namespaceId: string, abortController?: AbortController) => void
export const useEndpointList = (): [EndpointListAction, EndpointList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, endpointList, loading, error] = useGet<EndpointList>()
    const fn = useCallback(async (namespaceId: string, abortController?: AbortController) => get(`/v1/namespaces/${namespaceId}/endpoints`, abortController), [auth])
    return [fn, endpointList, loading, error]
}

export type EndpointCreateActionProps = {
    namespaceId: string;
    name: string;
    funcId: string;
    method: string;
    abortController?: AbortController;
}

type EndpointCreateAction = (props: EndpointCreateActionProps) => void
export const useEndpointCreate = (): [EndpointCreateAction, EndpointCreateSubmitted, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<EndpointCreateSubmitted>()
    const fn = useCallback(async ({namespaceId, name, funcId, method, abortController}: EndpointCreateActionProps) =>
        post(`/v1/namespaces/${namespaceId}/endpoints`, {
            name,
            funcId,
            method
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}
