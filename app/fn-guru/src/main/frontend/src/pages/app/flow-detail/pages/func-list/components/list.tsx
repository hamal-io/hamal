import React, {FC} from "react";
import {Separator} from "@/components/ui/separator.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/flow-detail/pages/func-list/components/create.tsx";
import {useApiFuncList} from "@/hook/api/func.ts";
import {ApiFlowSimple, ApiFuncSimple} from "@/api/types";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useNavigate} from "react-router-dom";

type ListProps = {
    flow: ApiFlowSimple
}

const List: FC<ListProps> = ({flow}) => {
    const [funcs, isLoading, error] = useApiFuncList(flow.id)

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Functions"
                description={`Functions of your flow ${flow.name}`}
                actions={[<Create flow={flow}/>]}
            />
            <Separator className="my-6"/>
            {
                funcs.length ? (<Content flowId={flow.id} funcs={funcs}/>) : (<NoContent flow={flow}/>)
            }
        </div>
    );
}

type ContentProps = {
    flowId: string;
    funcs: ApiFuncSimple[]
}

const Content: FC<ContentProps> = ({flowId, funcs}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-1 xl:grid-cols-3">
            {funcs.map((func) => (
                <Card
                    key={func.id}
                    className="relative overfunc-hidden duration-500 hover:border-primary/50 group"
                    onClick={() => {
                        navigate(`/flows/${flowId}/functions/${func.id}`)
                    }}
                >
                    <CardHeader>
                        <div className="flex items-center justify-between ">
                            <CardTitle>{func.name}</CardTitle>
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

type NoContentProps = {
    flow: ApiFlowSimple;
}
const NoContent: FC<NoContentProps> = ({flow}) => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Funcs found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Funcs yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create flow={flow}/>
            <GoToDocumentation link={"/funcs"}/>
        </div>
    </EmptyPlaceholder>
)

export default List;