import {useNavigate} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {useRecipeCreate} from "@/hook/recipe.ts";
import {Button} from "@/components/ui/button.tsx";
import {Loader2, Plus} from "lucide-react";

const CreateRecipe = () => {
    const navigate = useNavigate()
    const [createRecipe, submitted] = useRecipeCreate()
    const [isLoading, setLoading] = useState(false)

    async function create() {
        try {
            setLoading(true)
            createRecipe("New Recipe", "print('hamal')", "I will print 'hamal'")
        } catch (e) {
            console.log(e)
        }finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        const abortController = new AbortController();
        if (submitted) {
            navigate(`/recipes/editor/${submitted.id}`)
        }
        return () => {
            abortController.abort();
        }
    }, [submitted]);

    return (
        <Button type={"submit"} size="lg" onClick={create}>
            {isLoading ? <Loader2 className="mr-2 h-4 w-4 animate-spin"/> : <Plus className="w-4 h-4 mr-1"/>}
            Create Recipe
        </Button>
    )
}

export default CreateRecipe