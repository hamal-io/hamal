import {FlowList} from "@/types";
import {useGet} from "@/hook/http.ts";
import {useCallback} from "react";

type FlowListAction = (groupId: string, abortController?: AbortController) => void
export const useFlowList = (): [FlowListAction, FlowList, boolean, Error] => {
    const [get, flowList, loading, error] = useGet<FlowList>()
    const fn = useCallback(async (groupId: string, abortController?: AbortController) => get(`/v1/groups/${groupId}/flows`, abortController), [])
    return [fn, flowList, loading, error]
}