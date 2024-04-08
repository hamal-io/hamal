import {useCallback} from "react";
import {useGet, usePost} from "@/hooks/http.ts";
import {useAuth} from "@/hooks/auth.ts";

type FlowCreateAction = (
    name: string,
    code: string,
    abortController?: AbortController
) => void
export const useFlowCreate = (): [FlowCreateAction, FlowCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submitted, loading, error] = usePost<FlowCreateRequested>()
    const fn = useCallback<FlowCreateAction>(async (name: string, code, abortController?) =>
        post(`/v1/flows`, {name, code}, abortController), [auth]
    )
    return [fn, submitted, loading, error]
}

type FlowGetAction = (id: string, abortController?: AbortController) => void
export const useFlowGet = (): [FlowGetAction, FlowCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [get, flow, loading, error] = useGet<Flow>()
    const fn = useCallback<FlowGetAction>(async (id: string, abortController?) =>
        get(`/v1/flows/${id}`, abortController), [auth])
    return [fn, flow, loading, error]
}

type FlowListAction = (abortController?: AbortController) => void
export const useFlowList = (): [FlowListAction, FlowList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, flowList, loading, error] = useGet<FlowList>()
    const fn = useCallback<FlowListAction>(async (abortController?) =>
        get(`/v1/flows`, abortController), [auth])
    return [fn, flowList, loading, error]
}