import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {TriggerCreateSubmitted, TriggerList} from "@/types";

type TriggerListScheduleAction = (flowId: string, abortController?: AbortController) => void
export const useTriggerListSchedule = (): [TriggerListScheduleAction, TriggerList, boolean, Error] => {
    const [get, funcList, loading, error] = useGet<TriggerList>()
    const fn = useCallback(async (flowId: string, abortController?: AbortController) =>
        get(`/v1/flows/${flowId}/triggers?types=FixedRate`, abortController
        ), [])
    return [fn, funcList, loading, error]
}

type TriggerFixedRateCreateAction = (flowId: string, funcId: string, name: string, duration: string, abortController?: AbortController) => void
export const useTriggerFixedRateCreate = (): [TriggerFixedRateCreateAction, TriggerCreateSubmitted, boolean, Error] => {
    const [post, submission, loading, error] = usePost<TriggerCreateSubmitted>()
    const fn = useCallback(async (flowId: string, funcId: string, name: string, duration: string, abortController?: AbortController) =>
        post(`/v1/flows/${flowId}/triggers`, {
            type: "FixedRate",
            name,
            funcId,
            inputs: {},
            duration,
        }, abortController), []
    )
    return [fn, submission, loading, error]
}