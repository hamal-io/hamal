import {useGet} from "@/hook/http.ts";
import {useCallback} from "react";
import {Exec, ExecList} from "@/types";
import {useAuth} from "@/hook/auth.ts";

type ExecListAction = (namespaceId: string, abortController?: AbortController) => void
export const useExecList = (): [ExecListAction, ExecList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, execList, loading, error] = useGet<ExecList>()
    const fn = useCallback(async (namespaceId: string, abortController?: AbortController) => get(`/v1/namespaces/${namespaceId}/execs`, abortController), [auth])
    return [fn, execList, loading, error]
}

type ExecGetAction = (execId: string, abortController?: AbortController) => void
export const useExecGet = (): [ExecGetAction, Exec, boolean, Error] => {
    const [auth] = useAuth()
    const [get, exec, loading, error] = useGet<Exec>()
    const fn = useCallback(async (execId: string, abortController?: AbortController) => get(`/v1/execs/${execId}`, abortController), [auth])
    return [fn, exec, loading, error]
}