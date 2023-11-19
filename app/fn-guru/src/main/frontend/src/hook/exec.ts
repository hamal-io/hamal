import {useGet} from "@/hook/http.ts";
import {useCallback} from "react";
import {ExecList} from "@/types";
import {useAuth} from "@/hook/auth.ts";

type ExecListAction = (flowId: string, abortController?: AbortController) => void
export const useExecList = (): [ExecListAction, ExecList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, execList, loading, error] = useGet<ExecList>()
    const fn = useCallback(async (flowId: string, abortController?: AbortController) => get(`/v1/flows/${flowId}/execs`, abortController), [auth])
    return [fn, execList, loading, error]
}