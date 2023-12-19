import {useAuth} from "@/hook/auth.ts";
import {useCallback} from "react";
import {useGet} from "@/hook/http.ts";
import {Code} from "@/types/code.ts";


type CodeGetAction = (codeId: string, version: number, abortController?: AbortController) => void
export const useCodeGet = (): [CodeGetAction, Code, boolean, Error] => {
    const [auth] = useAuth()
    const [get, code, loading, error] = useGet<Code>()
    const fn = useCallback(async (codeId: string, version: number, abortController?: AbortController) =>
        get(`/v1/code/${codeId}?version=${version}`, abortController), [auth])
    return [fn, code, loading, error]
}

export type CodeCallback = (version: number) => void