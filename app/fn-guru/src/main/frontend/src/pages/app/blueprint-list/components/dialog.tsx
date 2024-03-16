import React, {FC, useEffect, useState} from "react";
import {useBlueprintGet} from "@/hook/blueprint.ts";
import {useNavigate} from "react-router-dom";
import {DialogContent, DialogDescription, DialogHeader} from "@/components/ui/dialog.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useAdhoc, useNamespaceList} from "@/hook";
import {BlueprintListItem} from "@/types/blueprint.ts";
import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {SelectValue} from "@radix-ui/react-select";

type Props = {
    item: BlueprintListItem
    onClose: () => void
}
export const BpDialog: FC<Props> = ({item, onClose}) => {
    const [uiState] = useUiState()
    const navigate = useNavigate()
    const [namespace, setNamespace] = useState(uiState.namespaceId)
    const [listNamespaces, namespaceList, loading, error] = useNamespaceList()
    const [adhoc, data] = useAdhoc()
    const [getBlueprint, blueprint] = useBlueprintGet()

    useEffect(() => {
        const abortController = new AbortController();
        if (uiState.workspaceId) {
            listNamespaces(uiState.workspaceId, abortController)
        }

        return () => {
            abortController.abort();
        }
    }, [uiState.workspaceId]);

    async function deployAction() {
        try {
            getBlueprint(item.id)
        } catch (e) {
            console.log(e)
        } finally {
            //onClose()
        }
    }

    useEffect(() => {
        if (blueprint) {
            adhoc(namespace, blueprint.value)
            onClose()
        }
    }, [blueprint]);

    if (loading) return `Error`
    if (namespaceList === null || loading) return "Loading..."

    return (
        <DialogContent>
            <DialogHeader>{item.name}</DialogHeader>
            <DialogDescription>{item.description}</DialogDescription>
            <div/>
            <div className="flex flex-row justify-between items-center gap-4">
                <Select
                    value={namespace}
                    onValueChange={(ns) => setNamespace(ns)}>
                    <SelectTrigger>
                        <SelectValue/>
                    </SelectTrigger>
                    <SelectContent>
                        <SelectGroup>
                            <SelectLabel>Deploy to Namespace: </SelectLabel>
                            {namespaceList.namespaces.map(namespace =>
                                <SelectItem
                                    key={namespace.id}
                                    value={namespace.id}>{namespace.name}
                                </SelectItem>
                            )}
                        </SelectGroup>
                    </SelectContent>
                </Select>
                <Button type={"submit"} size="sm" onClick={deployAction}>
                    Deploy
                </Button>
                <Button size={"sm"} onClick={() => {
                    navigate(`/blueprints/editor/${item.id}`)
                }} variant="secondary">
                    Edit
                </Button>
            </div>
        </DialogContent>
    )
}