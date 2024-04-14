import {PageHeader} from "@/components/page-header.tsx";
import React, {useState} from "react";
import TagFilter from "@/pages/app/recipe-list/components/tag-filter.tsx";
import {Recipe} from "@/types/recipe.ts";
import {tags} from "@/pages/app/recipe-list/components/tags.tsx";
import RecipeCard from "@/pages/app/recipe-list/components/card.tsx";

const RecipeListPage = () => {
    const [recipes, setRecipes] = useState<Recipe[]>(
        [
            {
                id: "1",
                name: "Ethereum Telegram Bridge",
                description: "Describe me please",
                tags: [tags.ethereum, tags.telegram],
            }
        ]
    )

    return (
        <main className="flex justify-center w-screen min-h-screen ">
            <div className="rounded-3xl w-11/12 h-5/6 md:w-9/12 md:h-5/6 overflow-y-auto">
                <PageHeader actions={[]}/>
                <section className={"flex flex-col gap-4"}>
                    <div>
                        <TagFilter/>
                    </div>
                    <div>
                        {
                            recipes.map(recipe => (
                                <RecipeCard recipe={recipe}/>
                            ))
                        }
                    </div>
                </section>
            </div>
        </main>
    )
}

export default RecipeListPage