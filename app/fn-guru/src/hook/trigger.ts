import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {TriggerCreateRequested, TriggerList, TriggerStatusRequested} from "@/types";
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

type TriggerListEndpointAction = (namespaceId: string, abortController?: AbortController) => void
export const useTriggerListEndpoint = (): [TriggerListEndpointAction, TriggerList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, funcList, loading, error] = useGet<TriggerList>()
    const fn = useCallback(async (namespaceId: string, abortController?: AbortController) =>
        get(`/v1/namespaces/${namespaceId}/triggers?types=Endpoint`, abortController
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

type TriggerEndpointCreateAction = (namespaceId: string, funcId: string, name: string, abortController?: AbortController) => void
export const useTriggerEndpointCreate = (): [TriggerEndpointCreateAction, TriggerCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerCreateRequested>()
    const fn = useCallback(async (namespaceId: string, funcId: string, name: string, abortController?: AbortController) =>
        post(`/v1/namespaces/${namespaceId}/triggers`, {
            type: "Endpoint",
            name,
            funcId,
            inputs: {},
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}

type TriggerHookCreateProps = {
    namespaceId: string;
    funcId: string;
    name: string;
    abortController?: AbortController;
}

type TriggerHookCreateAction = (props: TriggerHookCreateProps) => void
export const useTriggerHookCreate = (): [TriggerHookCreateAction, TriggerCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerCreateRequested>()
    const fn = useCallback(
        async ({namespaceId, funcId, name,  abortController}: TriggerHookCreateProps) =>
            post(`/v1/namespaces/${namespaceId}/triggers`, {
                type: "Hook",
                name,
                funcId,
                inputs: {}
            }, abortController), [auth])
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
    const fn = useCallback<TriggerEventCreateAction>(
        async ({namespaceId, topicId, funcId, name}: TriggerEventCreateProps, abortController?: AbortController) =>
            post(`/v1/namespaces/${namespaceId}/triggers`, {
                type: 'Event',
                name,
                funcId,
                topicId
            }, abortController),
        [auth])
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


type TriggerStatusAction = (triggerId: string, status: string, abortController?: AbortController) => void
export const useSetTriggerStatus = (): [TriggerStatusAction, TriggerStatusRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TriggerStatusRequested>()
    const fn = useCallback<TriggerStatusAction>(
        async (triggerId, status, abortController) => {
            post(`/v1/trigger/${triggerId}/${status}`, {}, abortController)
        }, [auth])
    return [fn, submission, loading, error]
}

type TriggerListAction = (namespaceId: string, abortController?: AbortController) => void
export const useTriggerList = (): [TriggerListAction, TriggerList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, triggerList, loading, error] = useGet<TriggerList>()
    const fn = useCallback<TriggerListAction>(async (namespaceId, abortController?) => {
            get(`/v1/namespaces/${namespaceId}/triggers`, abortController)
        }, [auth]
    )

    return [fn, triggerList, loading, error]
}



