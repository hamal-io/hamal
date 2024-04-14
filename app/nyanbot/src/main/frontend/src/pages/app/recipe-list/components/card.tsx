import {Card, CardTitle} from "@/components/ui/card.tsx";
import {Recipe} from "@/types/recipe.ts";
import {FC} from "react";
import {useNavigate} from "react-router-dom";

type Props = {
    recipe: Recipe

}
const RecipeCard: FC<Props> = ({recipe}) => {
    const navigate = useNavigate()

    function handleClick() {
        //navigate(`/recipes/${recipe.id}`)
    }


    return (
        <Card onClick={handleClick} className={"flex flex-row gap-4 p-4 justify-between items-center cursor-pointer"}>
            <div>
                <div>
                    <CardTitle>
                        {recipe.name}
                    </CardTitle>
                </div>
                <div>
                    {recipe.description}
                </div>
            </div>
            <div className={"flex flex-row gap-2"}>
                {
                    recipe.tags.map(tag =>
                        <p>{tag.name}</p>
                    )
                }
            </div>
        </Card>
    )
}

export default RecipeCard