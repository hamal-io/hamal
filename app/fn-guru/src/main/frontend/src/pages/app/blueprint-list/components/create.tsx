import {useNavigate} from "react-router-dom";
import React, {FC, useEffect, useState} from "react";
import {useBlueprintCreate} from "@/hook/blueprint.ts";
import {Button} from "@/components/ui/button.tsx";
import {Plus} from "lucide-react";

type Props = {}
export const CreateBlueprint: FC<Props> = () => {
    const navigate = useNavigate()
    const [isLoading, setLoading] = useState(false)
    const [createBlueprint, submitted] = useBlueprintCreate()

    async function create() {
        setLoading(true)
        try {
            createBlueprint("New Blueprint", "print('hamal')", "I will print 'hamal'")
        } catch (e) {
            console.log(e)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        const abortController = new AbortController();
        if (submitted !== null) {
            navigate(`/blueprints/editor/${submitted.id}`)
        }
        return () => {
            abortController.abort();
        }
    }, [submitted, navigate]);

    return (
        <Button type={"submit"} size="lg" onClick={create}>
            <Plus className="w-4 h-4 mr-1"/>
            Create Blueprint
        </Button>
    )
}