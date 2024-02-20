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

type DProps = {
    item: BlueprintListItem
}

export const BpDialog: FC<DProps> = ({item}) => {
    const navigate = useNavigate()
    const [namespaceId, setNamespaceId] = useState(null)


    const selHandler = (ns: string) => {
        setNamespaceId(ns)
    }

    return (
        <DialogContent>
            <DialogHeader>{item.name}</DialogHeader>
            <DialogDescription>{item.description}</DialogDescription>
            <div/>
            <div className="flex flex-row justify-between items-center">
                <NamespaceSelector selHandler={selHandler}/>
                <Deploy blueprintId={item.id} namespaceId={namespaceId}/>
                <Button size={"sm"}
                        onClick={() => {
                            navigate(`/blueprints/editor/${item.id}`)
                        }} variant="secondary">
                    Config
                </Button>
            </div>
        </DialogContent>
    )
}

type SelProps = {
    selHandler: (string) => void
}
const NamespaceSelector: FC<SelProps> = ({selHandler}) => {
    const [uiState] = useUiState()
    const [selected, setSelected] = useState(uiState.namespaceId)
    const [listNamespaces, namespaceList, loading] = useNamespaceList()

    useEffect(() => {
        if (uiState.workspaceId) {
            listNamespaces(uiState.workspaceId)
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
                    setSelected(newNamespaceId)
                    selHandler(selected)
                }
            }>
            <SelectTrigger>
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


type DeployProps = {
    blueprintId: string,
    namespaceId: string
}
const Deploy: FC<DeployProps> = ({blueprintId, namespaceId}) => {
    const [adhoc, data] = useAdhoc()
    const [getBlueprint, blueprint, loading, error] = useBlueprintGet()


    useEffect(() => {
        const abortController = new AbortController();
        getBlueprint(blueprintId)
        return () => {
            abortController.abort();
        };
    }, [blueprintId]);


    if (blueprint == null || loading) {
        return "Loading..."
    }

    async function deployAction() {
        if (blueprint !== null) {
            adhoc(namespaceId, blueprint.value)
        }
    }

    return (
        <Button type={"submit"} size="sm" onClick={deployAction}>
            Deploy
        </Button>
    )
}