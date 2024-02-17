import {Blueprint, BlueprintCreateRequested, BlueprintList} from "@/types/blueprint.ts";
import {useAuth} from "@/hook/auth.ts";
import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";


type BlueprintCreateAction = (name: string, value: string, description: string, abortController?: AbortController) => void
export const useBlueprintCreate = (): [BlueprintCreateAction, BlueprintCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<BlueprintCreateRequested>()
    const fn = useCallback(async (name: string, value: string, description: string, abortController?: AbortController) =>
        post(`/v1/blueprints`, {
            name,
            inputs: {},
            value,
            description
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}

type BlueprintGetAction = (bpId: string, abortController?: AbortController) => void
export const useBlueprintGet = (): [BlueprintGetAction, Blueprint, boolean, Error] => {
    const [auth] = useAuth()
    const [get, bp, loading, error] = useGet<Blueprint>()
    const fn = useCallback(async (bpId: string, abortController?: AbortController) =>
        get(`/v1/blueprints/${bpId}`, abortController), [auth])
    return [fn, bp, loading, error]
}

type BlueprintListAction = (abortController?: AbortController) => void
export const useBlueprintList = (): [BlueprintListAction, BlueprintList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, blueprintList, loading, error] = useGet<BlueprintList>()
    const fn = useCallback(async (abortController?: AbortController) =>
        get(`/v1/blueprints`, abortController), [auth])
    return [fn, blueprintList, loading, error]
}