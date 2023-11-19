import {useCallback} from "react";
import {useGet} from "@/hook/http.ts";
import {ExecLogList} from "@/types";
import {useAuth} from "@/hook/auth.ts";

type ExecLogListAction = (execId: string, abortController?: AbortController) => void
export const useExecLogList = (): [ExecLogListAction, ExecLogList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, execList, loading, error] = useGet<ExecLogList>()
    const fn = useCallback(async (execId: string, abortController?: AbortController) => get(`/v1/execs/${execId}/logs`, abortController), [auth])
    return [fn, execList, loading, error]
}
