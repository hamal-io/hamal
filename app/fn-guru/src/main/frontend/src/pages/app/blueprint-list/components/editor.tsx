import React, {useEffect} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {useBlueprintGet} from "@/hook/blueprint.ts";
import {useNavigate, useParams} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";
import BlueprintEditorForm from "@/pages/app/blueprint-list/components/form.tsx";


const BlueprintEditor = () => {
    const {blueprintId} = useParams()
    const navigate = useNavigate()
    const [getBlueprint, blueprint, loading] = useBlueprintGet()

    useEffect(() => {
        getBlueprint(blueprintId)
    }, [blueprintId]);


    if (blueprint == null || loading) return "Loading..."

    return (
        <div className="h-full pt-4">
            <div className="container flex flex-row justify-between items-center ">
                <PageHeader
                    title="Blueprint Editor"
                    actions={[
                        <div className="flex w-full space-x-2 justify-end">
                            <Button variant={"secondary"}
                                    onClick={() => navigate("/blueprints")}
                            >
                                Return to list
                            </Button>
                        </div>

                    ]}
                />
            </div>
            <div className="bg-white container h-full py-6">
                {blueprint &&
                    <BlueprintEditorForm
                        blueprintId={blueprint.id}
                        name = {blueprint.name}
                        description={blueprint.description}
                        value={blueprint.value}
                    />
                }
            </div>

        </div>
    )
}

export default BlueprintEditor
