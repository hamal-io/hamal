import {Blueprint, BlueprintCreateRequested, BlueprintList, BlueprintUpdateRequested} from "@/types/blueprint.ts";
import {useAuth} from "@/hook/auth.ts";
import {useGet, usePatch, usePost} from "@/hook/http.ts";
import {useCallback} from "react";


type BlueprintCreateAction = (name: string, value: string, description: string, abortController?: AbortController) => void
export const useBlueprintCreate = (): [BlueprintCreateAction, BlueprintCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submitted, loading, error] = usePost<BlueprintCreateRequested>()
    const fn = useCallback(async (name: string, value: string, description: string, abortController?: AbortController) =>
        post(`/v1/blueprints`, {
            name: name,
            inputs: {},
            value: value,
            description: description
        }, abortController), [auth]
    )
    return [fn, submitted, loading, error]
}

type BlueprintGetAction = (bpId: string, abortController?: AbortController) => void
export const useBlueprintGet = (): [BlueprintGetAction, Blueprint, boolean, Error] => {
    const [auth] = useAuth()
    const [get, bp, loading, error] = useGet<Blueprint>()
    const fn = useCallback(async (bpId: string, abortController?: AbortController) =>
        get(`/v1/blueprints/${bpId}`, abortController), [auth])
    return [fn, bp, loading, error]
}


type BlueprintUpdateAction = (id: string, name: string, value: string, description: string, abortController?: AbortController) => void
export const useBlueprintUpdate = (): [BlueprintUpdateAction, BlueprintUpdateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [patch, submitted, loading, error] = usePatch<BlueprintUpdateRequested>()
    const fn = useCallback(async (id: string, name: string, value: string, description: string, abortController?: AbortController) =>
        patch(`/v1/blueprints/${id}`, {
            name: name,
            inputs: {},
            value: value,
            description: description
        }, abortController), [auth])
    return [fn, submitted, loading, error]
}


type BlueprintListAction = (abortController?: AbortController) => void
export const useBlueprintList = (): [BlueprintListAction, BlueprintList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, list, loading, error] = useGet<BlueprintList>()
    const fn = useCallback(async (abortController?: AbortController) =>
        get(`/v1/blueprints`, abortController), [auth])
    return [fn, list, loading, error]
}