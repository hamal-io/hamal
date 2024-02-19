import React, {FC, useEffect} from "react";
import {useBlueprintGet} from "@/hook/blueprint.ts";
import {useNavigate} from "react-router-dom";
import {DialogContent, DialogHeader} from "@/components/ui/dialog.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useAdhoc} from "@/hook";

type DProps = { id: string }
export const DDialog: FC<DProps> = ({id}) => {
    const [getBlueprint, blueprint, loading, error] = useBlueprintGet()
    const navigate = useNavigate()

    useEffect(() => {
        const abortController = new AbortController();
        getBlueprint(id, abortController)
        return () => {
            abortController.abort();
        }
    }, [id]);

    if (blueprint == null || loading) return "Loading..."

    return (
        <DialogContent>
            <DialogHeader>{blueprint.name}</DialogHeader>
            <p>{blueprint.description}</p>
            <Deploy blueprintId={blueprint.id}/>
            <Button size={"sm"}
                    onClick={() => {
                        navigate(`/blueprints/editor/${blueprint.id}`)
                    }} variant="secondary">
                Config
            </Button>
        </DialogContent>
    )
}

type DeployProps = { blueprintId: string }
const Deploy: FC<DeployProps> = ({blueprintId}) => {
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
            adhoc(uiState.namespaceId, blueprint.value)
        }
    }

    return (
        <Button type={"submit"} size="sm" onClick={setup}>
            Deploy
        </Button>
    )
}