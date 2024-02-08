import React, {FC, useEffect} from "react";
import {Separator} from "@/components/ui/separator.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/flow-detail/pages/hook-list/components/create.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useNavigate} from "react-router-dom";
import {useHookList} from "@/hook/hook.ts";
import Detail from "@/pages/app/flow-detail/pages/hook-list/components/detail.tsx";
import {useTriggerListHook} from "@/hook";
import {HookWithTriggers} from "@/pages/app/flow-detail/pages/hook-list/type.tsx";

type flowProps = {
    id: string;
    name: string;
}

type ListProps = {
    flow: flowProps
}

const List: FC<ListProps> = ({flow}) => {
    const [listHooks, hookList, hooksLoading, hooksError] = useHookList()
    const [listTriggers, triggerList, triggerLoading, triggerError] = useTriggerListHook()

    useEffect(() => {
        const abortController = new AbortController();
        listHooks(flow.id, abortController)
        listTriggers(flow.id, abortController)
        return () => {
            abortController.abort();
        };
    }, [flow]);

    if (hooksError || triggerError) return `Error`
    if (hooksLoading || triggerLoading) return "Loading..."

    const hooksWithTrigger = hookList.hooks.map<HookWithTriggers>(hook => {
        return {
            hook: hook,
            trigger: triggerList.triggers.filter(trigger => trigger.hook?.id === hook.id)
        }
    })

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Webhooks"
                description={`Webhooks of ${flow.name}`}
                actions={[<Create flow={flow}/>]}
            />
            <Separator className="my-6"/>
            {
                hookList.hooks.length ? (<Content flowId={flow.id} items={hooksWithTrigger}/>) : (<NoContent flow={flow}/>)
            }
        </div>
    );
}

type ContentProps = {
    flowId: string;
    items: HookWithTriggers[]
}

const Content: FC<ContentProps> = ({flowId, items}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-1 xl:grid-cols-3">
            {items.map((item) => (
                <Detail key={item.hook.id} item={item}/>
            ))}
        </ul>
    )
}

type NoContentProps = {
    flow: flowProps;
}
const NoContent: FC<NoContentProps> = ({flow}) => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Webhooks found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Webhook yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create flow={flow}/>
            <GoToDocumentation link={"/hooks"}/>
        </div>
    </EmptyPlaceholder>
)

export default List;