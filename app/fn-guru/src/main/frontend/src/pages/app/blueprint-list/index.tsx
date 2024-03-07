import React, {useEffect} from "react";
import {useBlueprintList} from "@/hook/blueprint.ts";
import {PageHeader} from "@/components/page-header.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import CreateBlueprint from "@/pages/app/blueprint-list/components/create.tsx";
import BlueprintCard from "@/pages/app/blueprint-list/components/card.tsx";


const BlueprintListPage = () => {
    const [listBlueprints, blueprintList, loading, error] = useBlueprintList()

    useEffect(() => {
        listBlueprints()
    }, []);

    if (error) return `Error`
    if (blueprintList === null || loading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Blueprints"
                description={'Tryout our predefined workflows, proudly brought to you by the hamal.io team.'}
                actions={[<CreateBlueprint/>]}
            />
            {blueprintList ?
                <ol className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 cursor-pointer">
                    {blueprintList.blueprints.map(item =>
                        <li key={item.id}>
                            <BlueprintCard blueprint={item}/>
                        </li>
                    )}
                </ol> : <NoContent/>}
        </div>

    )
}

const NoContent = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Title>No Blueprints found.</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>

        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <CreateBlueprint/>
        </div>
    </EmptyPlaceholder>
)

export default BlueprintListPage