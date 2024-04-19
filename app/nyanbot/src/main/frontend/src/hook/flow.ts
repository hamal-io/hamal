import {useCallback, useEffect, useRef} from "react";
import {useGet, usePost} from "@/hook/http.ts";
import {useAuth} from "@/hook/auth.ts";
import {Flow, FlowList} from "@/types/flow.ts";
import {Namespace, NamespaceAppendRequested, NamespaceList} from "@/types/namespace.ts";

type FlowCreateAction = (
    /*name: string,
    triggerType: string,
    code: string,*/
    name: string,
    namespaceId: string,
    abortController?: AbortController
) => void
export const useFlowCreate = (): [FlowCreateAction, Flow, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submitted, loading, error] = usePost<NamespaceAppendRequested>()
    const flow = useRef<Flow>(null)
    const fn = useCallback<FlowCreateAction>(async (namespaceId, name, abortController?) => {
            post(`/v1/namespaces/${namespaceId}/namespaces`, {name}, abortController)

            useEffect(() => {
                if (submitted) {
                    flow.current = {
                        id: submitted.id,
                        name: name,
                        status: "Inactive"
                    }
                }
            }, [submitted])
        }, [auth]
    )


    return [fn, flow.current, loading, error]
}

type FlowGetAction = (flowId: string, abortController?: AbortController) => void
export const useFlowGet = (): [FlowGetAction, Flow, boolean, Error] => {
    const [auth] = useAuth()
    const [get, flow, loading, error] = useGet<Namespace>()
    const fn = useCallback<FlowGetAction>(async (flowId, abortController?) =>
        get(`/v1/namespaces/${flowId}`, abortController), [auth])
    return [fn, flow, loading, error]
}

type FlowListAction = (workspaceId: string, abortController?: AbortController) => void
export const useFlowList = (): [FlowListAction, FlowList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, flowList, loading, error] = useGet<NamespaceList>()
    const fn = useCallback<FlowListAction>(async (workspaceId, abortController?) => {
            get(`/v1/workspaces/${workspaceId}/namespaces`, abortController)

        }
        , [auth])
    return [fn, flowList, loading, error]
}


//FIXME 310 TriggerId...
type FlowStatusAction = (flowId: string, status: string, abortController?: AbortController) => void
export const useSetFlowStatus = (): [FlowStatusAction, Flow, boolean, Error] => {
    const [auth] = useAuth()
    const [post, flow, loading, error] = usePost<Flow>()

    const fn = useCallback<FlowStatusAction>(async (flowId, status, abortController) =>
        post(`/v1/flows/${flowId}/${status}`, {}, abortController), [auth])
    return [fn, flow, loading, error]
}
*/
