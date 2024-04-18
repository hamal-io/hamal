import React, {useEffect} from "react";
import {useRecipeList} from "@/hook/recipe.ts";
import {PageHeader} from "@/components/page-header.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import CreateRecipe from "@/pages/app/recipe-list/components/create.tsx";
import RecipeCard from "@/pages/app/recipe-list/components/card.tsx";


const RecipePage = () => {
    const [listRecipes, recipeList, loading, error] = useRecipeList()

    useEffect(() => {
        try {
            listRecipes()
        } catch (e){
            console.log(e)
        }

    }, []);

    if (error) return `Error`
    if (recipeList === null || loading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Recipes"
                description={'Tryout our predefined workflows, proudly brought to you by the hamal.io team.'}
                actions={[<CreateRecipe/>]}
            />

            {recipeList.recipes.length !== 0 ?
                <ol className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 cursor-pointer">
                    {recipeList.recipes.map(item =>
                        <li key={item.id}>
                            <RecipeCard recipe={item}/>
                        </li>
                    )}
                </ol> : <NoContent/>}
        </div>

    )
}

const NoContent = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Title>No Recipes found.</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>

        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <CreateRecipe/>
        </div>
    </EmptyPlaceholder>
)

export default RecipePage