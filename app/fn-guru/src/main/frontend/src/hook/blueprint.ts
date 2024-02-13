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

type BlueprintListAction = (groupId: string, abortController?: AbortController) => void
export const useFuncList = (): [BlueprintListAction, BlueprintList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, bps, loading, error] = useGet<BlueprintList>()
    const fn = useCallback(async (groupId: string, abortController?: AbortController) =>
        get(`/v1/groups/${groupId}/blueprints`, abortController), [auth])
    return [fn, bps, loading, error]
}