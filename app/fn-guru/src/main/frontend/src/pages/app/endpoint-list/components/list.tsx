import React, {FC, useEffect} from "react";
import {Separator} from "@/components/ui/separator.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/endpoint-list/components/create.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useNavigate} from "react-router-dom";
import {useEndpointList} from "@/hook/endpoint.ts";
import Detail from "@/pages/app/endpoint-list/components/detail.tsx";
import {EndpointListItem} from "@/types";

type GroupProps = {
    id: string;
    name: string;
}

type ListProps = {
    group: GroupProps
}

const List: FC<ListProps> = ({group}) => {
    const [listEndpoints, endpointList, endpointsLoading, endpointsError] = useEndpointList()
    const [listTriggers, triggerList, triggerLoading, triggerError] = useEndpointList()

    useEffect(() => {
        const abortController = new AbortController();
        listEndpoints(group.id, abortController)
        listTriggers(group.id, abortController)
        return () => {
            abortController.abort();
        };
    }, [group]);

    if (endpointsError || triggerError) return `Error`
    if (endpointsLoading || triggerLoading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Endpoints"
                description={`Endpoints of ${group.name}`}
                actions={[<Create group={group}/>]}
            />
            <Separator className="my-6"/>
            {
                endpointList.endpoints.length ? (<Content groupId={group.id} endpoints={endpointList.endpoints}/>) : (<NoContent group={group}/>)
            }
        </div>
    );
}

type ContentProps = {
    groupId: string;
    endpoints: Array<EndpointListItem>
}

const Content: FC<ContentProps> = ({groupId, endpoints}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-1 xl:grid-cols-3">
            {endpoints.map((endpoint) => (
                <Detail key={endpoint.id} item={endpoint}/>
            ))}
        </ul>
    )
}

type NoContentProps = {
    group: GroupProps;
}
const NoContent: FC<NoContentProps> = ({group}) => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Endpoints found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Endpoints yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create group={group}/>
            <GoToDocumentation link={"/endpoints"}/>
        </div>
    </EmptyPlaceholder>
)

export default List;