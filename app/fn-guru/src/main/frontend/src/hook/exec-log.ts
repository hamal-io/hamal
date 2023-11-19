import {useCallback} from "react";
import {useGet} from "@/hook/http.ts";
import {ExecLogList} from "@/types";

type ExecLogListAction = (execId: string, abortController?: AbortController) => void
export const useExecLogList = (): [ExecLogListAction, ExecLogList, boolean, Error] => {
    const [get, execList, loading, error] = useGet<ExecLogList>()
    const fn = useCallback(async (execId: string, abortController?: AbortController) => get(`/v1/execs/${execId}/logs`, abortController), [])
    return [fn, execList, loading, error]
}
