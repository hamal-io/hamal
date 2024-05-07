import {PageHeader} from "@/components/page-header.tsx";
import React, {useState} from "react";
import TagFilter from "@/pages/app/recipe-list/components/tag-filter.tsx";
import {Recipe} from "@/types/recipe.ts";
import {tags} from "@/pages/app/recipe-list/components/tags.tsx";
import RecipeCard from "@/pages/app/recipe-list/components/card.tsx";

const RecipeListPage = () => {
    const [recipes] = useState<Recipe[]>(
        [
            {
                id: "1",
                name: "Ethereum And Telegram",
                description: "Describe me please",
                tags: [tags.ethereum, tags.telegram],
            },
            {
                id: "2",
                name: "Just Ethereum",
                description: "Describe me please",
                tags: [tags.ethereum],
            },
            {
                id: "3",
                name: "Just Telegram",
                description: "Describe me please",
                tags: [tags.telegram],
            }
        ]
    )

    const [filtered, setFiltered] = useState(recipes)

    function handleFilter(filters: Set<string>) {
        if (filters.size === 0) {
            setFiltered(recipes)
        } else {
            setFiltered(recipes.filter(recipe =>
                filters.size === recipe.tags.length && recipe.tags.every(tag => filters.has(tag.name))
            ));
        }
    }

    return (
        <main className="flex justify-center w-screen min-h-screen ">
            <div className="rounded-3xl w-11/12 h-5/6 md:w-9/12 md:h-5/6 overflow-y-auto">
                <PageHeader actions={[]}/>
                <section className={"flex flex-col gap-4"}>
                    <div>
                        <TagFilter onChange={handleFilter}/>
                    </div>
                    <div>
                        <ol className={"flex flex-col gap-4"}>
                            {filtered.map(recipe => (
                                <li key={recipe.id}>
                                    <RecipeCard recipe={recipe}/>
                                </li>
                            ))}
                        </ol>
                    </div>
                </section>
            </div>
        </main>
    )
}

export default RecipeListPage