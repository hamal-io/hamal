import {Blueprint, BlueprintList} from "@/types/blueprint.ts";
import {useAuth} from "@/hook/auth.ts";
import {useGet} from "@/hook/http.ts";
import {useCallback} from "react";

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