import {Workspace} from "@/types";
import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {useAuth} from "@/hook/auth.ts";

type WorkspaceGetAction = (workspaceId: string, abortController?: AbortController) => void
export const useWorkspaceGet = (): [WorkspaceGetAction, Workspace, boolean, Error] => {
    const [auth] = useAuth()
    const [get, groupList, loading, error] = useGet<Workspace>()
    const fn = useCallback(async (workspaceId: string, abortController?: AbortController) => get(`/v1/workspaces/${workspaceId}`, abortController), [auth])
    return [fn, groupList, loading, error]
}
