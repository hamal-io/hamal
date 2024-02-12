import React, {FC, useContext, useEffect} from "react";
import {GroupLayoutContext} from "@/components/app/layout";
import {useHookList, useTriggerListHook} from "@/hook";
import {HookWithTriggers} from "@/pages/app/hook-list/type.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/hook-list/components/create.tsx";
import {Separator} from "@/components/ui/separator.tsx";
import {useNavigate} from "react-router-dom";
import Detail from "@/pages/app/hook-list/components/detail.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";

type Props = {}
const HookListPage: FC<Props> = ({}) => {
    const {namespaceId, groupId} = useContext(GroupLayoutContext)
    const [listHooks, hookList, hooksLoading, hooksError] = useHookList()
    const [listTriggers, triggerList, triggerLoading, triggerError] = useTriggerListHook()

    useEffect(() => {
        const abortController = new AbortController();
        listHooks(namespaceId, abortController)
        listTriggers(namespaceId, abortController)
        return () => {
            abortController.abort();
        };
    }, [namespaceId]);

    if (hooksError || triggerError) return `Error`
    if (hookList == null || triggerList == null || hooksLoading || triggerLoading) return "Loading..."

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
                description={`Webhooks of namespace TBD`}
                actions={[<Create/>]}
            />
            {
                hookList.hooks.length ? (<Content items={hooksWithTrigger}/>) : (<NoContent/>)
            }
        </div>
    );
}

type ContentProps = {
    items: HookWithTriggers[]
}

const Content: FC<ContentProps> = ({items}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-1 xl:grid-cols-3">
            {items.map((item) => (
                <Detail key={item.hook.id} item={item}/>
            ))}
        </ul>
    )
}

const NoContent = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Webhooks found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Webhook yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create/>
            <GoToDocumentation link={"/hooks"}/>
        </div>
    </EmptyPlaceholder>
)


export default HookListPage