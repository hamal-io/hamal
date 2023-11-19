import React, {FC, useEffect} from "react";
import {Separator} from "@/components/ui/separator.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/flow-list/components/create.tsx";
import {ApiFlowSimple} from "@/api/types";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useNavigate} from "react-router-dom";
import {useFlowList} from "@/hook/flow.ts";

type ListProps = {
    groupId: string
}

const List: FC<ListProps> = ({groupId}) => {
    const [listFlows, flowList, isLoading, error] = useFlowList()

    useEffect(() => {
        const abortController = new AbortController()
        listFlows(groupId, abortController)
        return () => {
            abortController.abort()
        }
    }, [groupId]);

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    // const filteredFlows = flowList.flows.filter(flow => flow.name !== '__default__')
    const filteredFlows = flowList.flows

    return (
        <div className="pt-8 px-8">
            <PageHeader title="Workflows" description="Organise your workflows" actions={[<Create/>]}/>
            <Separator className="my-6"/>
            {
                filteredFlows.length ? (<Content flows={filteredFlows}/>) : (<NoContent/>)
            }
        </div>
    );
}

type ContentProps = {
    flows: ApiFlowSimple[]
}

const Content: FC<ContentProps> = ({flows}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8">
            {flows.map((flow) => (
                <Card
                    key={flow.id}
                    className="relative overflow-hidden duration-500 hover:border-primary/50 group"
                    onClick={() => {
                        navigate(`/flows/${flow.id}`)
                    }}
                >
                    <CardHeader>
                        <div className="flex items-center justify-between ">
                            <CardTitle>{flow.name}</CardTitle>
                        </div>
                    </CardHeader>
                    <CardContent>
                        <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                            <div className="flex justify-between py-3 gap-x-4">
                            </div>
                        </dl>
                    </CardContent>
                </Card>
            ))}
        </ul>
    )
}

const NoContent: FC = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Flows found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Flows yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create/>
            <GoToDocumentation link={"/flows"}/>
        </div>
    </EmptyPlaceholder>
)

export default List;