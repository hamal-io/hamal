import {useGet} from "@/hook/http.ts";
import {useCallback} from "react";
import {ExecList} from "@/types";
import {useAuth} from "@/hook/auth.ts";

type ExecListAction = (namespaceId: string, abortController?: AbortController) => void
export const useExecList = (): [ExecListAction, ExecList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, execList, loading, error] = useGet<ExecList>()
    const fn = useCallback(async (namespaceId: string, abortController?: AbortController) => get(`/v1/namespaces/${namespaceId}/execs`, abortController), [auth])
    return [fn, execList, loading, error]
}