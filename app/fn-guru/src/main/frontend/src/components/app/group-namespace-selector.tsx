import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {SelectValue} from "@radix-ui/react-select";
import React, {FC, useEffect, useState} from "react";

import {cn} from "@/utils"
import {useNamespaceList} from "@/hook/namespace.ts";
import {useLocation, useNavigate} from "react-router-dom";

type Props = {
    className?: string;
    groupId: string;
    namespaceId: string;
}
const GroupNamespaceSelector: FC<Props> = ({className, groupId, namespaceId}) => {
    const [listNamespaces, namespaceList, loading] = useNamespaceList()
    const [selected, setSelected] = useState(namespaceId)
    const navigate = useNavigate()
    const location = useLocation()

    useEffect(() => {
        if (groupId) {
            listNamespaces(groupId)
        }
    }, [groupId]);

    if (loading) {
        return "Loading..."
    }

    return (
        <Select value={selected} onValueChange={
            (newNamespaceId) => {
                navigate(location.pathname.replace(`namespaces/${selected}/`, `namespaces/${newNamespaceId}/`), {replace: true})
                setSelected(newNamespaceId)

            }
        }>
            <SelectTrigger className={cn("h-8 bg-white", className)}>
                <SelectValue/>
            </SelectTrigger>
            <SelectContent>
                <SelectGroup>
                    <SelectLabel>Namespaces</SelectLabel>
                    {namespaceList.namespaces.map(namespace =>
                        <SelectItem
                            key={namespace.id}
                            value={namespace.id}>{namespace.name}
                        </SelectItem>
                    )}
                </SelectGroup>
            </SelectContent>
        </Select>
    )
}

export default GroupNamespaceSelector