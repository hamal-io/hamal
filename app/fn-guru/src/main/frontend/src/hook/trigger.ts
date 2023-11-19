import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {TriggerCreateSubmitted, TriggerList} from "@/types";
import {useAuth} from "@/hook/auth.ts";

type TriggerListScheduleAction = (flowId: string, abortController?: AbortController) => void
export const useTriggerListSchedule = (): [TriggerListScheduleAction, TriggerList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, funcList, loading, error] = useGet<TriggerList>()
    const fn = useCallback(async (flowId: string, abortController?: AbortController) =>
        get(`/v1/flows/${flowId}/triggers?types=FixedRate`, abortController
        ), [auth])
    return [fn, funcList, loading, error]
}

type TriggerListHookAction = (flowId: string, abortController?: AbortController) => void
export const useTriggerListHook = (): [TriggerListScheduleAction, TriggerList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, funcList, loading, error] = useGet<TriggerList>()
    const fn = useCallback(async (flowId: string, abortController?: AbortController) =>
        get(`/v1/flows/${flowId}/triggers?types=Hook`, abortController
        ), [auth])
    return [fn, funcList, loading, error]
}

type TriggerFixedRateCreateAction = (flowId: string, funcId: string, name: string, duration: string, abortController?: AbortController) => void
export const useTriggerFixedRateCreate = (): [TriggerFixedRateCreateAction, TriggerCreateSubmitted, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerCreateSubmitted>()
    const fn = useCallback(async (flowId: string, funcId: string, name: string, duration: string, abortController?: AbortController) =>
        post(`/v1/flows/${flowId}/triggers`, {
            type: "FixedRate",
            name,
            funcId,
            inputs: {},
            duration,
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}

type TriggerHookCreateAction = (flowId: string, funcId: string, name: string, hookId: string, abortController?: AbortController) => void
export const useTriggerHookCreate = (): [TriggerHookCreateAction, TriggerCreateSubmitted, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerCreateSubmitted>()
    const fn = useCallback(async (flowId: string, funcId: string, name: string, hookId: string, abortController?: AbortController) =>
        post(`/v1/flows/${flowId}/triggers`, {
            type: "Hook",
            name,
            funcId,
            inputs: {},
            hookId,
            hookMethods: ["Post"]
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}