import React, {FC, useEffect, useState} from "react";
import {useBlueprintGet} from "@/hook/blueprint.ts";
import {useNavigate} from "react-router-dom";
import {DialogContent, DialogHeader} from "@/components/ui/dialog.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useAdhoc} from "@/hook";
import {BlueprintListItem} from "@/types/blueprint.ts";

type DProps = {
    item: BlueprintListItem
}
export const DDialog: FC<DProps> = ({item}) => {
    const navigate = useNavigate()
    const [uiState] = useUiState()
    const [namespaceId, setNamespace] = useState(uiState.namespaceId)


    return (
        <DialogContent>
            <DialogHeader>{item.name}</DialogHeader>
            <p>{item.description}</p>
            <Deploy blueprintId={item.id} namespaceId={namespaceId}/>
            <Button size={"sm"}
                    onClick={() => {
                        navigate(`/blueprints/editor/${item.id}`)
                    }} variant="secondary">
                Config
            </Button>
        </DialogContent>
    )
}

type DeployProps = {
    blueprintId: string,
    namespaceId: string
}
const Deploy: FC<DeployProps> = ({blueprintId, namespaceId}) => {
    const [uiState] = useUiState()
    const [adhoc, data] = useAdhoc()
    const [getBlueprint, blueprint, loading, error] = useBlueprintGet()


    useEffect(() => {
        const abortController = new AbortController();
        getBlueprint(blueprintId)
        return () => {
            abortController.abort();
        };
    }, [blueprintId]);

    async function setup() {
        if (blueprint !== null) {
            adhoc(namespaceId, blueprint.value)
        }
    }

    return (
        <Button type={"submit"} size="sm" onClick={setup}>
            Deploy
        </Button>
    )
}