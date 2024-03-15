import React, {useEffect, useState} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import Editor from "@/components/editor.tsx";
import {useBlueprintGet, useBlueprintUpdate} from "@/hook/blueprint.ts";
import {useNavigate, useParams} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";
import {Input} from "@/components/ui/input.tsx";

const BlueprintEditor = () => {
    const {bpId} = useParams()
    const navigate = useNavigate()
    const [getBlueprint, blueprint, loading, getError] = useBlueprintGet()
    const [isLoading, setLoading] = useState(false)
    const [updateBlueprint] = useBlueprintUpdate()

    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [value, setValue] = useState('')


    useEffect(() => {
        const abortController = new AbortController();
        getBlueprint(bpId, abortController)
        return () => {
            abortController.abort();
        }
    }, [bpId]);

    useEffect(() => {
        if (blueprint !== null) {
            setName(blueprint.name)
            setDescription(blueprint.description)
            setValue(blueprint.value)
        }
    }, [blueprint]);


    async function onSave() {
        setLoading(true)
        const abortController = new AbortController();
        try {
            updateBlueprint(blueprint.id, name, value, description)
        } catch (e) {
            console.error(e)
        } finally {
            setLoading(false)
            abortController.abort();
        }
    }

    if (blueprint == null || loading) return "Loading..."

    return (
        <div className="h-full pt-4">
            <div className="container flex flex-row justify-between items-center ">
                <PageHeader
                    title="Blueprint Editor"
                    actions={[
                        <div className="flex w-full space-x-2 justify-end">
                            <Button type={"submit"}
                                    onClick={() => {
                                        onSave()
                                    }
                                    }> Save</Button>

                            <Button variant={"secondary"}
                                    onClick={() => {
                                        navigate("/blueprints")
                                    }
                                    }> Return to list</Button>
                        </div>

                    ]}
                />
            </div>

            <div className="bg-white container h-full py-6">
                <div>
                    <label>
                        Name:
                        <Input
                            type={"text"}
                            onChange={name => setName(name.target.value)}
                            value={name}
                        ></Input>
                    </label>

                    <label>
                        Description:
                        <Input
                            type={"text"}
                            onChange={desc => setDescription(desc.target.value)}
                            value={description}
                        ></Input>
                    </label>
                </div>
                <label>
                    Value:
                    <div className="bg-white p-4 rounded-sm border-2">
                        <Editor code={value} onChange={setValue}/>

                    </div>
                </label>
            </div>
        </div>
    )
}


export default BlueprintEditor
