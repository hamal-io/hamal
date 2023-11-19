import React, {FC, useEffect} from "react";
import {Separator} from "@/components/ui/separator.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/flow-detail/pages/hook-list/components/create.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useNavigate} from "react-router-dom";
import {useHookList} from "@/hook/hook.ts";
import {HookListItem} from "@/types";

type FlowProps = {
    id: string;
    name: string;
}

type ListProps = {
    flow: FlowProps
}

const List: FC<ListProps> = ({flow}) => {
    const [listHooks, hookList, loading, error] = useHookList()

    useEffect(() => {
        const abortController = new AbortController();
        listHooks(flow.id, abortController)
        return () => {
            abortController.abort();
        };
    }, [flow]);

    if (error) return `Error`
    if (hookList == null || loading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Hooks"
                description={`Hooks of ${flow.name}`}
                actions={[<Create flow={flow}/>]}
            />
            <Separator className="my-6"/>
            {
                hookList.hooks.length ? (<Content flowId={flow.id} hooks={hookList.hooks}/>) : (<NoContent flow={flow}/>)
            }
        </div>
    );
}

type ContentProps = {
    flowId: string;
    hooks: HookListItem[]
}

const Content: FC<ContentProps> = ({flowId, hooks}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-1 xl:grid-cols-3">
            {hooks.map((hook) => (
                <Card
                    key={hook.id}
                    className="relative overhook-hidden duration-500 hover:border-primary/50 group"
                >
                    <CardHeader>
                        <div className="flex items-center justify-between ">
                            <CardTitle>{hook.name}</CardTitle>
                        </div>
                    </CardHeader>
                    <CardContent>
                        <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                            <div className="flex justify-between py-3 gap-x-4">
                                http://localhost:5173/v1/webhooks/{hook.id}
                            </div>
                        </dl>
                    </CardContent>
                </Card>
            ))}
        </ul>
    )
}

type NoContentProps = {
    flow: FlowProps;
}
const NoContent: FC<NoContentProps> = ({flow}) => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Hooks found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Hooks yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create flow={flow}/>
            <GoToDocumentation link={"/hooks"}/>
        </div>
    </EmptyPlaceholder>
)

export default List;