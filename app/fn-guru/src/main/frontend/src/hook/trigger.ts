import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {TriggerCreateRequested, TriggerList, TriggerStatus, TriggerStatusRequested} from "@/types";
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
export const useTriggerListHook = (): [TriggerListHookAction, TriggerList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, hookList, loading, error] = useGet<TriggerList>()
    const fn = useCallback(async (namespaceId: string, abortController?: AbortController) =>
        get(`/v1/namespaces/${namespaceId}/triggers?types=Hook`, abortController
        ), [auth])
    return [fn, hookList, loading, error]
}

type TriggerFixedRateCreateAction = (namespaceId: string, funcId: string, name: string, duration: string, abortController?: AbortController) => void
export const useTriggerFixedRateCreate = (): [TriggerFixedRateCreateAction, TriggerCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerCreateRequested>()
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
export const useTriggerHookCreate = (): [TriggerHookCreateAction, TriggerCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerCreateRequested>()
    const fn = useCallback(async ({
                                      namespaceId,
                                      funcId,
                                      name,
                                      hookId,
                                      hookMethod,
                                      abortController
                                  }: TriggerHookCreateProps) =>
        post(`/v1/namespaces/${namespaceId}/triggers`, {
            type: "Hook",
            name,
            funcId,
            inputs: {},
            hookId,
            hookMethod
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}

export type TriggerEventCreateProps = {
    name: string,
    namespaceId: string,
    topicId: string,
    funcId: string
}

type TriggerEventCreateAction = (props: TriggerEventCreateProps, abortController?: AbortController) => void
export const useTriggerEventCreate = (): [TriggerEventCreateAction, TriggerCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerCreateRequested>()
    const fn = useCallback<TriggerEventCreateAction>(async ({
                                                                namespaceId,
                                                                topicId,
                                                                funcId,
                                                                name
                                                            }: TriggerEventCreateProps, abortController?: AbortController) =>
        post(`/v1/namespaces/${namespaceId}/triggers`, {
            type: 'Event',
            name,
            funcId,
            topicId
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}

type TriggerListEventAction = (namespaceId: string, abortController?: AbortController) => void
export const useTriggerListEvent = (): [TriggerListEventAction, TriggerList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, triggerList, loading, error] = useGet<TriggerList>()
    const fn = useCallback(async (namespaceId: string, abortController?: AbortController) =>
        get(`/v1/namespaces/${namespaceId}/triggers?types=Event`, abortController
        ), [auth])
    return [fn, triggerList, loading, error]
}

export type TriggerStatus = 'activate' | 'deactivate'
type TriggerStatusAction = (triggerId: string, status: TriggerStatus, abortController?: AbortController) => void
export const useTriggerStatus = (): [TriggerStatusAction, TriggerStatusRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerStatusRequested>()
    const fn = useCallback<TriggerStatusAction>(
        async (triggerId, status, abortController) =>
        post(`/trigger/${triggerId}/${status}`, {}, abortController), [auth])
    return [fn, submission, loading, error]
}