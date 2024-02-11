import React, {FC, useContext, useEffect} from "react";
import {GroupLayoutContext} from "@/components/app/layout";
import {useEndpointList} from "@/hook/endpoint.ts";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/endpoint-list/components/create.tsx";
import {Separator} from "@/components/ui/separator.tsx";
import {EndpointListItem} from "@/types";
import {useNavigate} from "react-router-dom";
import Detail from "@/pages/app/endpoint-list/components/detail.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";

type Props = {}
const EndpointListPage: FC<Props> = ({}) => {
    const {namespaceId, groupId} = useContext(GroupLayoutContext)

    const [listEndpoints, endpointList, endpointsLoading, endpointsError] = useEndpointList()
    const [listTriggers, triggerList, triggerLoading, triggerError] = useEndpointList()

    useEffect(() => {
        const abortController = new AbortController();
        listEndpoints(namespaceId, abortController)
        listTriggers(namespaceId, abortController)
        return () => {
            abortController.abort();
        };
    }, [namespaceId]);

    if (endpointsError || triggerError) return `Error`
    if (endpointsLoading || triggerLoading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Endpoints"
                description={`Endpoints of namespace TBD`}
                actions={[<Create/>]}
            />
            <Separator className="my-6"/>
            {
                endpointList.endpoints.length ? (<Content endpoints={endpointList.endpoints}/>) : (<NoContent/>)
            }
        </div>
    );
}

type ContentProps = {
    endpoints: Array<EndpointListItem>
}

const Content: FC<ContentProps> = ({endpoints}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-1 xl:grid-cols-3">
            {endpoints.map((endpoint) => (
                <Detail key={endpoint.id} item={endpoint}/>
            ))}
        </ul>
    )
}

const NoContent = ({}) => (
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