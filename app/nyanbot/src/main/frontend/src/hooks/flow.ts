import {useCallback} from "react";
import {useGet, usePost} from "@/hooks/http.ts";
import {useAuth} from "@/hooks/auth.ts";

type FlowCreateAction = (id: string, name: string, abortController?: AbortController) => void
const useFlowCreate = (): [FlowCreateAction, FlowCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submitted, loading, error] = usePost<FlowCreateRequested>()
    const fn = useCallback<FlowCreateAction>(async (id: string, name: string, abortController?) =>
        post(`/v1/flows`, {id, name}, abortController), [auth]
    )
    return [fn, submitted, loading, error]
}

type FlowGetAction = (id: string, abortController?: AbortController) => void
const useFlowCreate = (): [FlowGetAction, FlowCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [get, flow, loading, error] = useGet<Flow>()
    const fn = useCallback<FlowGetAction>(async (id: string, abortController?) =>
        get(`/v1/flows/${id}`, abortController), [auth])
    return [fn, flow, loading, error]
}

type FlowListAction = (abortController?: AbortController) => void
const useFlowList = (): [FlowListAction, FlowList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, flowList, loading, error] = useGet<FlowList>()
    const fn = useCallback<FlowListAction>(async (abortController?) =>
        get(`/v1/flows`, abortController), [auth])
    return [fn, flowList, loading, error]
}