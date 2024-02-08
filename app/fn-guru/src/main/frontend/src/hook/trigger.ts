import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {TriggerCreateSubmitted, TriggerList} from "@/types";
import {useAuth} from "@/hook/auth.ts";

type TriggerListScheduleAction = (namespaceId: string, abortController?: AbortController) => void
export const useTriggerListSchedule = (): [TriggerListScheduleAction, TriggerList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, funcList, loading, error] = useGet<TriggerList>()
    const fn = useCallback(async (namespaceId: string, abortController?: AbortController) =>
        get(`/v1/namespaces/${namespaceId}/triggers?types=FixedRate`, abortController
        ), [auth])
    return [fn, funcList, loading, error]
}

type TriggerListHookAction = (namespaceId: string, abortController?: AbortController) => void
export const useTriggerListHook = (): [TriggerListScheduleAction, TriggerList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, funcList, loading, error] = useGet<TriggerList>()
    const fn = useCallback(async (namespaceId: string, abortController?: AbortController) =>
        get(`/v1/namespaces/${namespaceId}/triggers?types=Hook`, abortController
        ), [auth])
    return [fn, funcList, loading, error]
}

type TriggerFixedRateCreateAction = (namespaceId: string, funcId: string, name: string, duration: string, abortController?: AbortController) => void
export const useTriggerFixedRateCreate = (): [TriggerFixedRateCreateAction, TriggerCreateSubmitted, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerCreateSubmitted>()
    const fn = useCallback(async (namespaceId: string, funcId: string, name: string, duration: string, abortController?: AbortController) =>
        post(`/v1/namespaces/${namespaceId}/triggers`, {
            type: "FixedRate",
            name,
            funcId,
            inputs: {},
            duration,
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}

type TriggerHookCreateProps = {
    namespaceId: string;
    hookId: string;
    funcId: string;
    name: string;
    hookMethod: string;
    abortController?: AbortController;
}

type TriggerHookCreateAction = (props: TriggerHookCreateProps) => void
export const useTriggerHookCreate = (): [TriggerHookCreateAction, TriggerCreateSubmitted, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerCreateSubmitted>()
    const fn = useCallback(async ({namespaceId, funcId, name, hookId, hookMethod, abortController}: TriggerHookCreateProps) =>
        post(`/v1/namespaces/${namespaceId}/triggers`, {
            type: "Hook",
            name,
            funcId,
            inputs: {},
            hookId,
            hookMethods: [hookMethod]
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}