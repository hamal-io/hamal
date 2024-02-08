import {Group} from "@/types";
import {useGet, usePost} from "@/hook/http.ts";
import {useCallback} from "react";
import {useAuth} from "@/hook/auth.ts";

type GroupGetAction = (groupId: string, abortController?: AbortController) => void
export const useGroupGet = (): [GroupGetAction, Group, boolean, Error] => {
    const [auth] = useAuth()
    const [get, groupList, loading, error] = useGet<Group>()
    const fn = useCallback(async (groupId: string, abortController?: AbortController) => get(`/v1/groups/${groupId}`, abortController), [auth])
    return [fn, groupList, loading, error]
}
