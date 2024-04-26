import {useCallback, useEffect, useState} from "react";
import {useGet, usePost} from "@/hook/http.ts";
import {useAuth} from "@/hook/auth.ts";
import {Flow, FlowCreateRequested, FlowList} from "@/types/flow.ts";
import {NamespaceList} from "@/types/namespace.ts";

type FlowCreateAction = (
    /*triggerType: string,
    code: string,*/
    name: string,
    namespaceId: string,
    abortController?: AbortController
) => void
export const useFlowCreate = (): [FlowCreateAction, FlowCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submitted, loading, error] = usePost<FlowCreateRequested>()
    const fn = useCallback<FlowCreateAction>(async (name, namespaceId, abortController?) =>
        post(`/v1/namespaces/${namespaceId}/namespaces`, {name: name}, abortController), [auth]
    )

    return [fn, submitted, loading, error]
}

type FlowGetAction = (flowId: string, abortController?: AbortController) => void
export const useFlowGet = (): [FlowGetAction, Flow, boolean, Error] => {
    const [auth] = useAuth()
    const [get, flow, loading, error] = useGet<Flow>()
    const fn = useCallback<FlowGetAction>(async (flowId, abortController?) =>
        get(`/v1/namespaces/${flowId}`, abortController), [auth]
    )

    return [fn, flow, loading, error]
}

type FlowListAction = (workspaceId: string, abortController?: AbortController) => void
export const useFlowList = (): [FlowListAction, FlowList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, namespaceList, loading, error] = useGet<NamespaceList>()
    const [flowList, setFlowList] = useState<FlowList>(null)
    const fn = useCallback<FlowListAction>(async (workspaceId, abortController?) =>
        get(`/v1/workspaces/${workspaceId}/namespaces`, abortController), [auth])

    useEffect(() => {
        if (namespaceList) {
            const _flows = namespaceList.namespaces.map(namespace => {
                return {
                    id: namespace.id,
                    name: namespace.name.split("::").pop(),
                    status: "Inactive"
                }
            })
            _flows.pop()
            setFlowList(_flows)
        }
    }, [namespaceList]);
    return [fn, flowList, loading, error]
}


type FlowStatusAction = (triggerId: string, status: string, abortController?: AbortController) => void
export const useSetFlowStatus = (): [FlowStatusAction, Flow, boolean, Error] => {
    const [auth] = useAuth()
    const [post, flow, loading, error] = usePost<Flow>()
    const fn = useCallback<FlowStatusAction>(async (triggerId, status, abortController) =>
        post(`/v1/trigger/${triggerId}/${status}`, {}, abortController), [auth])

    return [fn, flow, loading, error]
}

