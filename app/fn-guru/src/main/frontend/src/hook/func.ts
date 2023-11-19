import {ApiFunc} from "@/api/types";
import {useCallback, useState} from "react";
import {useGetAction} from "@/hook/http.ts";
import {Func} from "@/types/func.ts";

type FuncGetAction = (funcId: string) => void
export const useFuncGetAction = (): [FuncGetAction, ApiFunc, boolean, Error] => {
    const [get, func, loading, error] = useGetAction<Func>()
    const fn = useCallback(async (funcId: string) => get(`v1/funcs/${funcId}`), [])
    return [fn, func, loading, error]
}
