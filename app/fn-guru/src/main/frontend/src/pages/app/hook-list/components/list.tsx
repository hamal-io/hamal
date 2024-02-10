import React, {FC, useEffect} from "react";
import {Separator} from "@/components/ui/separator.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/hook-list/components/create.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useNavigate} from "react-router-dom";
import {useHookList} from "@/hook/hook.ts";
import Detail from "@/pages/app/hook-list/components/detail.tsx";
import {useTriggerListHook} from "@/hook";
import {HookWithTriggers} from "@/pages/app/hook-list/type.tsx";

type GroupProps = {
    id: string;
    name: string;
}

type ListProps = {
    group: GroupProps
}

const List: FC<ListProps> = ({group}) => {
    const [listHooks, hookList, hooksLoading, hooksError] = useHookList()
    const [listTriggers, triggerList, triggerLoading, triggerError] = useTriggerListHook()

    useEffect(() => {
        const abortController = new AbortController();
        listHooks(group.id, abortController)
        listTriggers(group.id, abortController)
        return () => {
            abortController.abort();
        };
    }, [group]);

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
                description={`Webhooks of ${group.name}`}
                actions={[<Create group={group}/>]}
            />
            <Separator className="my-6"/>
            {
                hookList.hooks.length ? (<Content groupId={group.id} items={hooksWithTrigger}/>) : (<NoContent flow={group}/>)
            }
        </div>
    );
}

type ContentProps = {
    groupId: string;
    items: HookWithTriggers[]
}

const Content: FC<ContentProps> = ({groupId, items}) => {
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
    flow: GroupProps;
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
            <Create group={flow}/>
            <GoToDocumentation link={"/hooks"}/>
        </div>
    </EmptyPlaceholder>
)

export default List;