import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {SelectValue} from "@radix-ui/react-select";
import React, {FC, useEffect, useState} from "react";

import {cn} from "@/utils"
import {useNamespaceList} from "@/hook/namespace.ts";
import {useLocation, useNavigate} from "react-router-dom";
import {useChangeGroup, useChangeNamespace, useUiState} from "@/hook/ui-state.ts";

type Props = {
    className?: string;
}
const GroupNamespaceSelector: FC<Props> = ({className}) => {
    const [listNamespaces, namespaceList, loading] = useNamespaceList()
    const [uiState] = useUiState()
    const [selected, setSelected] = useState(uiState.namespaceId)
    const [changeNamespace] = useChangeNamespace()
    const navigate = useNavigate()
    const location = useLocation()

    useEffect(() => {
        if (uiState.groupId) {
            listNamespaces(uiState.groupId)
        }
    }, [uiState.namespaceId]);

    if (namespaceList == null || loading) {
        return "Loading..."
    }

    return (
        <Select
            value={selected}
            onValueChange={
                (newNamespaceId) => {
                    changeNamespace(newNamespaceId, "changed")
                    navigate(location.pathname.replace(`namespaces/${selected}/`, `namespaces/${newNamespaceId}/`), {replace: true})
                    setSelected(newNamespaceId)

                }
            }>
            <SelectTrigger className={cn("h-8 bg-white", className)}>
                <SelectValue/>
            </SelectTrigger>
            <SelectContent>
                <SelectGroup>
                    <SelectLabel>Current Namespace</SelectLabel>
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