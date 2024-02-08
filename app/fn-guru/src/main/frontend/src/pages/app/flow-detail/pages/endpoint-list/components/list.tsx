import React, {FC, useEffect} from "react";
import {Separator} from "@/components/ui/separator.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/flow-detail/pages/endpoint-list/components/create.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useNavigate} from "react-router-dom";
import {useEndpointList} from "@/hook/endpoint.ts";
import Detail from "@/pages/app/flow-detail/pages/endpoint-list/components/detail.tsx";
import {EndpointListItem} from "@/types";

type flowProps = {
    id: string;
    name: string;
}

type ListProps = {
    flow: flowProps
}

const List: FC<ListProps> = ({flow}) => {
    const [listEndpoints, endpointList, endpointsLoading, endpointsError] = useEndpointList()
    const [listTriggers, triggerList, triggerLoading, triggerError] = useEndpointList()

    useEffect(() => {
        const abortController = new AbortController();
        listEndpoints(flow.id, abortController)
        listTriggers(flow.id, abortController)
        return () => {
            abortController.abort();
        };
    }, [flow]);

    if (endpointsError || triggerError) return `Error`
    if (endpointsLoading || triggerLoading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Endpoints"
                description={`Endpoints of ${flow.name}`}
                actions={[<Create flow={flow}/>]}
            />
            <Separator className="my-6"/>
            {
                endpointList.endpoints.length ? (<Content flowId={flow.id} endpoints={endpointList.endpoints}/>) : (<NoContent flow={flow}/>)
            }
        </div>
    );
}

type ContentProps = {
    flowId: string;
    endpoints: Array<EndpointListItem>
}

const Content: FC<ContentProps> = ({flowId, endpoints}) => {
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
    flow: flowProps;
}
const NoContent: FC<NoContentProps> = ({flow}) => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Endpoints found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Endpoints yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create flow={flow}/>
            <GoToDocumentation link={"/endpoints"}/>
        </div>
    </EmptyPlaceholder>
)

export default List;