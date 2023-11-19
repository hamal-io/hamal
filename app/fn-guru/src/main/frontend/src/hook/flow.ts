import {Flow, FlowCreateSubmitted, FlowList, FuncCreateSubmitted} from "@/types";
import {useGet, usePost} from "@/hook/http.ts";
import {useCallback, useState} from "react";

type FlowGetAction = (flowId: string, abortController?: AbortController) => void
export const useFlowGet = (): [FlowGetAction, Flow, boolean, Error] => {
    const [get, flowList, loading, error] = useGet<Flow>()
    const fn = useCallback(async (flowId: string, abortController?: AbortController) => get(`/v1/flows/${flowId}`, abortController), [])
    return [fn, flowList, loading, error]
}

type FlowListAction = (groupId: string, abortController?: AbortController) => void
export const useFlowList = (): [FlowListAction, FlowList, boolean, Error] => {
    const [get, flowList, loading, error] = useGet<FlowList>()
    const fn = useCallback(async (groupId: string, abortController?: AbortController) => get(`/v1/groups/${groupId}/flows`, abortController), [])
    return [fn, flowList, loading, error]
}

type FlowCreateAction = (groupId: string, name: string, controller?: AbortController) => void
export const useFlowCreate = (): [FlowCreateAction, FlowCreateSubmitted, boolean, Error] => {
    const [post, submission, loading, error] = usePost<FlowCreateSubmitted>()
    const fn = useCallback(async (groupId: string, name: string, abortController?: AbortController) =>
        post(`/v1/groups/${groupId}/flows`, {
            name,
            inputs: {}
        }, abortController), []
    )
    return [fn, submission, loading, error]
}
