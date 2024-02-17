import {useUiState} from "@/hook/ui-state.ts";
import React, {FC, useEffect, useState} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import Editor from "@/components/editor.tsx";
import {useBlueprintGet, useBlueprintUpdate} from "@/hook/blueprint.ts";
import {useParams} from "react-router-dom";

type Props = {}
const BlueprintEditor: FC<Props> = () => {
    const {bpId} = useParams()
    const [uiState] = useUiState()
    const [getBlueprint, blueprint, getLoad, getError] = useBlueprintGet()
    const [isLoading, setLoading] = useState(false)
    const [updateBlueprint, submittedUpdate, updateLoad, errorLoad] = useBlueprintUpdate()
    const [code, setCode] = useState("empty blueprint")

    const executeUpdate = () => {
    } //TODO-196

    useEffect(() => {
        const abortController = new AbortController();
        getBlueprint(bpId, abortController)
        return () => {
            abortController.abort();
        }
    }, [bpId]);

    useEffect(() => {
        if (blueprint !== null) {
            setCode(blueprint.value)
        }
    }, [blueprint]);

    if (blueprint == null || getLoad) return "Loading..."
    return (
        <div className="h-full pt-4">
            <div className="container flex flex-row justify-between items-center ">
                <PageHeader
                    title="Blueprint Editor"
                    description="keep it coming"
                    actions={[]}
                />
                <div className="flex w-full space-x-2 justify-end">

                </div>
            </div>

            <div className="container h-full py-6">
                <div className="bg-white p-4 rounded-sm border-2">
                    <Editor code={code} onChange={setCode}/>
                </div>
            </div>
        </div>
    )
}

export default BlueprintEditor
