import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {SelectValue} from "@radix-ui/react-select";
import React, {FC, useEffect, useState} from "react";

import {cn} from "@/utils"
import {useNavigate} from "react-router-dom";
import {useFuncList} from "@/hook/func.ts";

type Props = {
    className?: string;
    groupId: string;
    funcId: string;
}
const GroupFuncSelector: FC<Props> = ({className, groupId, funcId}) => {
    const navigate = useNavigate()
    const [listFuncs, funcList, loading] = useFuncList()
    const [selected, setSelected] = useState(funcId)

    useEffect(() => {
        if (groupId) {
            listFuncs(groupId)
        }
    }, [groupId]);

    if (loading) {
        return "Loading..."
    }

    return (
        <Select value={selected} onValueChange={
            (newFuncId) => {
                navigate(`/groups/${groupId}/functions/${newFuncId}`, {replace: true})
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

export default GroupFuncSelector