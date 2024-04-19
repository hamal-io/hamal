import React, {useEffect} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {FlowCard} from "@/pages/app/flow-list/components/card.tsx";
import Create from "@/pages/app/flow-list/components/create.tsx";
import {EmptyPlaceholder} from "@/components/ui/empty-placeholder.tsx";
import {Code2, WorkflowIcon} from "lucide-react";
import {useNamespaceList} from "@/hook/namespace.ts";
import {useUiState} from "@/hook/ui.ts";


const FlowListPage = () => {
    const [uiState] = useUiState();
    const [listFlows, flowList, isLoading, error] = useNamespaceList()

    useEffect(() => {
        const abortController = new AbortController();
        listFlows(uiState.workspaceId, abortController)
        return (() => abortController.abort())
    }, []);


    if (!flowList || isLoading) return "Loading..."
    if (error) return "Error" // FIXME


    return (
        <>
            <main className="flex justify-center w-screen min-h-screen ">
                <div className="rounded-3xl w-11/12 h-5/6 md:w-9/12 md:h-5/6 overflow-y-auto">
                    <PageHeader actions={[
                        <Create/>
                    ]}/>
                    {flowList.namespaces.length !== 0 ?
                        <ol className="flex flex-col gap-4 cursor-pointer">
                            {flowList.namespaces.map(flow =>
                                <FlowCard key={flow.id} flow={flow}/>
                            )}
                        </ol> : <NoContent/>

                    }
                </div>
            </main>
        </>
    )
}

const NoContent = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            <WorkflowIcon/>
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Flows found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Flows yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create/>
        </div>
    </EmptyPlaceholder>
)

export default FlowListPage;