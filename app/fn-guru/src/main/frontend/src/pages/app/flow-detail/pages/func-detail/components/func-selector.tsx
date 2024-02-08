import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {SelectValue} from "@radix-ui/react-select";
import React, {FC, useEffect, useState} from "react";

import {cn} from "@/utils"
import {useNavigate} from "react-router-dom";
import {useFuncList} from "@/hook/func.ts";

type Props = {
    className?: string;
    flowId: string;
    funcId: string;
}
const FuncSelector: FC<Props> = ({className, flowId, funcId}) => {
    const navigate = useNavigate()
    const [listFuncs, funcList, loading] = useFuncList()
    const [selected, setSelected] = useState(funcId)

    useEffect(() => {
        if (flowId) {
            listFuncs(flowId)
        }
    }, [flowId]);

    if (loading) {
        return "Loading..."
    }

    return (
        <Select value={selected} onValueChange={
            (newFuncId) => {
                navigate(`/flows/${flowId}/functions/${newFuncId}`, {replace: true})
                setSelected(newFuncId)
            }
        }>
            <SelectTrigger className={cn("h-8 bg-white", className)}>
                <SelectValue/>
            </SelectTrigger>
            <SelectContent>
                <SelectGroup>
                    <SelectLabel>Functions</SelectLabel>
                    {funcList.funcs.map(func =>
                        <SelectItem key={func.id} value={func.id}>{func.name}</SelectItem>
                    )}
                </SelectGroup>
            </SelectContent>
        </Select>
    )
}

export default FuncSelector