import React, {FC, useEffect} from "react";
import {useTriggerListHook} from "@/hook";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/hook-list/components/create.tsx";
import Detail from "@/pages/app/hook-list/components/detail.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {TriggerListItem} from "@/types";

type Props = {}
const HookListPage: FC<Props> = ({}) => {
    const [uiState] = useUiState()
    const [listTriggers, triggerList, triggerLoading, triggerError] = useTriggerListHook()

    useEffect(() => {
        const abortController = new AbortController();
        listTriggers(uiState.namespaceId, abortController)
        return () => {
            abortController.abort();
        };
    }, [uiState.namespaceId]);

    if (triggerError || triggerError) return `Error`
    if (triggerList == null ||  triggerLoading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Webhooks"
                description={`Webhooks of namespace TBD`}
                actions={[<Create/>]}
            />
            {
                triggerList.triggers.length ? (<Content items={triggerList.triggers}/>) : (<NoContent/>)
            }
        </div>
    );
}

type ContentProps = {
    items: TriggerListItem[]
}

const Content: FC<ContentProps> = ({items}) => {
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