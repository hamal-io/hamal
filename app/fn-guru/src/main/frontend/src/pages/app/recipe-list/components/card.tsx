import {RecipeListItem} from "@/types/recipe.ts";
import React, {FC, useState} from "react";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Dialog} from "@/components/ui/dialog.tsx";
import {RecipeDialog} from "@/pages/app/recipe-list/components/dialog.tsx";

type CardProps = {
    recipe: RecipeListItem

}
const RecipeCard: FC<CardProps> = ({recipe}) => {
    const [open, setOpen] = useState(false)

    return (
        <>
            <Card
                onClick={() => setOpen(true)}
                className="relative overfunc-hidden duration-500 hover:border-primary/50 group pointerCursor"
            >
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle>{recipe.name}</CardTitle>
                </CardHeader>
                <CardContent>
                    <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                        <div className="flex justify-between py-3 gap-x-4">
                            {recipe.description}
                        </div>
                    </dl>
                </CardContent>
            </Card>
            <Dialog open={open} onOpenChange={setOpen}>
                <RecipeDialog item={recipe} onClose={() => setOpen(false)}/>
            </Dialog>
        </>
    )
}

export default RecipeCard

