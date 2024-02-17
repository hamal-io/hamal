import React, {FC, useEffect} from "react";
import {useBlueprintList} from "@/hook/blueprint.ts";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import CreateBlueprint from "@/pages/app/blueprint-list/components/create.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useNavigate} from "react-router-dom";

type Props = {}
const BlueprintListPage: FC<Props> = ({}) => {
    const [listBlueprints, blueprintList, loading, error] = useBlueprintList()

    useEffect(() => {
        const abortController = new AbortController();
        listBlueprints(abortController)
        return () => {
            abortController.abort();
        }
    }, [listBlueprints]);


    if (error) return `Error`
    if (blueprintList == null || loading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Blueprints"
                description={'Tryout our predefined workflows, proudly brought to you by the hamal.io team.'}
                actions={[<CreateBlueprint/>]}
            />
            {
                blueprintList.blueprints.length ?
                    (<BlueprintCards blueprints={blueprintList.blueprints}/>) : (<NoContent/>)
            }


        </div>
    );
}

type CardProps = {
    blueprints
}
const BlueprintCards: FC<CardProps> = ({blueprints}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            {blueprints.map((bp) => (
                <Card
                    key={bp.id}
                    className="relative overfunc-hidden duration-500 hover:border-primary/50 group"

                >
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle>{bp.name}</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                            <div className="flex justify-between py-3 gap-x-4">
                                {bp.description}
                            </div>
                        </dl>
                        <div className="flex flex-row justify-between items-center">
                            <Button onClick={() => {
                                console.log("nice try")
                            }} variant="secondary">
                                Run
                            </Button>
                            <Button onClick={() => {
                                navigate(`/blueprints/editor/${bp.id}`)
                            }} variant="secondary">
                                Config
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            ))}
        </ul>)

}


const NoContent = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Blueprints found.</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>

        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <CreateBlueprint/>
        </div>
    </EmptyPlaceholder>
)


export default BlueprintListPage