import React, {FC,  useEffect} from "react";
import {useTriggerListEndpoint} from "@/hook/trigger.ts";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/endpoint-list/components/create.tsx";
import { TriggerListItem} from "@/types";
import Detail from "@/pages/app/endpoint-list/components/detail.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useUiState} from "@/hook/ui-state.ts";

type Props = {}
const EndpointListPage: FC<Props> = () => {
    const [uiState] = useUiState()
    const [listTriggers, triggerList, triggerLoading, triggerError] = useTriggerListEndpoint()

    useEffect(() => {
        const abortController = new AbortController();
        listTriggers(uiState.namespaceId, abortController)
        return () => {
            abortController.abort();
        };
    }, [uiState.namespaceId]);

    if (triggerError || triggerError) return `Error`
    if (listTriggers == null || triggerList == null || triggerLoading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Endpoints"
                description={`Endpoints of namespace TBD`}
                actions={[<Create/>]}
            />
            {
                triggerList.triggers.length ? (<Content triggers={triggerList.triggers}/>) : (<NoContent/>)
            }
        </div>
    );
}

type ContentProps = {
    triggers: Array<TriggerListItem>
}

const Content: FC<ContentProps> = ({triggers}) => {
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-1 xl:grid-cols-3">
            {triggers.map((trigger) => (
                <Detail key={trigger.id} item={trigger}/>
            ))}
        </ul>
    )
}

const NoContent = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Endpoints found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Endpoints yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create/>
            <GoToDocumentation link={"/endpoints"}/>
        </div>
    </EmptyPlaceholder>
)

export default EndpointListPage