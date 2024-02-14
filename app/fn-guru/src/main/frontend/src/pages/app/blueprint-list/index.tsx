import React, {FC, useEffect} from "react";
import {useBlueprintList} from "@/hook/blueprint.ts";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {BlueprintListItem} from "@/types/blueprint.ts";

type Props = {}
const BlueprintListPage: FC<Props> = ({}) => {
    const [listBlueprints, blueprintList, loading, error] = useBlueprintList()

    useEffect(() => {
        const abortController = new AbortController();
        listBlueprints(abortController)
        return () => {
            abortController.abort();
        };
    });

    if (error) return `Error`
    if (blueprintList == null || loading) return "Loading..."

    return (
        <ul className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            {blueprintList.blueprints.map((item) => (
                BlueprintCard(item)
            ))}


        </ul>
    );
}

const BlueprintCard = (item: BlueprintListItem) => {

    return (<Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
                ${item.name}
            </CardTitle>
            <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                className="h-4 w-4 text-muted-foreground"
            >
                <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
            </svg>
        </CardHeader>
        <CardContent>
            <div className="text-2xl font-bold">$45,231.89</div>
            <p className="text-xs text-muted-foreground">
                ${item.description.value}
            </p>
        </CardContent>
    </Card>)

}


export default BlueprintListPage