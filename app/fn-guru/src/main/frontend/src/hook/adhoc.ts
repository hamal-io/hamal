import {ExecInvokeRequested} from "@/types";
import {usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {useAuth} from "@/hook/auth.ts";

type AdhocAction = (namespaceId: string, code: string, codeType: string, controller?: AbortController) => void
export const useAdhoc = (): [AdhocAction, ExecInvokeRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<ExecInvokeRequested>()
    const fn = useCallback(async (namespaceId: string, code: string, codeType: string, abortController?: AbortController) =>
        post(`/v1/namespaces/${namespaceId}/adhoc`, {
            code,
            inputs: {},
            codeType
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}
