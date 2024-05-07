import {PageHeader} from "@/components/page-header.tsx";
import React from "react";
import {useParams} from "react-router-dom";

const RecipeDetailPage = () => {
    const {recipeId} = useParams()


    return (
        <div className="pt-2 px-2">
            <PageHeader
                title={"recipe.name"}
                description={""}
                actions={[]}
            />
            {recipeId}
        </div>
    )
}

export default RecipeDetailPage
