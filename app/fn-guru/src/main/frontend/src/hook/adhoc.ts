import {ExecInvokeSubmitted} from "@/types";
import {usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {useAuth} from "@/hook/auth.ts";

type AdhocAction = (code: string, controller?: AbortController) => void
export const useAdhoc = (): [AdhocAction, ExecInvokeSubmitted, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<ExecInvokeSubmitted>()
    const fn = useCallback(async (code: string, abortController?: AbortController) =>
        post(`/v1/flows/${auth.defaultFlowIds[auth.groupId]}/adhoc`, {
            code,
            inputs: {}
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}
