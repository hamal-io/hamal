import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {SelectValue} from "@radix-ui/react-select";
import React, {FC, useContext, useEffect, useState} from "react";

import {cn} from "@/utils"
import {useNavigate} from "react-router-dom";
import {useFuncList} from "@/hook/func.ts";
import {GroupLayoutContext} from "@/components/app/layout";

type Props = {
    className?: string;
    funcId: string;
}
const GroupFuncSelector: FC<Props> = ({className, funcId}) => {
    const navigate = useNavigate()
    const {groupId, namespaceId} = useContext(GroupLayoutContext)
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
                navigate(`/groups/${groupId}/namespaces/${namespaceId}/functions/${newFuncId}`, {replace: true})
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