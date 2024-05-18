import React, {useEffect} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {useRecipeGet} from "@/hook/recipe.ts";
import {useNavigate, useParams} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";
import RecipeEditorForm from "@/pages/app/recipe-list/components/form.tsx";


const RecipeEditor = () => {
    const {recipeId} = useParams()
    const navigate = useNavigate()
    const [getRecipe, recipe, loading] = useRecipeGet()

    useEffect(() => {
        getRecipe(recipeId)
    }, [recipeId]);


    if (recipe == null || loading) return "Loading..."

    return (
        <div className="h-full pt-4">
            <div className="container flex flex-row justify-between items-center ">
                <PageHeader
                    title="Recipe Editor"
                    actions={[
                        <div className="flex w-full space-x-2 justify-end">
                            <Button variant={"secondary"}
                                    onClick={() => navigate("/recipes")}
                            >
                                Return to list
                            </Button>
                        </div>

                    ]}
                />
            </div>
            <div className="bg-white container h-full py-6">
                {recipe &&
                    <RecipeEditorForm
                        recipeId={recipe.id}
                        name = {recipe.name}
                        description={recipe.description}
                        value={recipe.value}
                    />
                }
            </div>

        </div>
    )
}

export default RecipeEditor
