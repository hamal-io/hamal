import React, {FC, useEffect, useState} from 'react'
import {useUiState} from "@/hook/ui-state.ts";
import NamespaceNodeEntry from "@/pages/app/workspace-detail/tab/namespace-list/components/node.tsx";
import {useNamespaceTree} from "@/pages/app/workspace-detail/tab/namespace-list/components/hook.ts";
import {PageHeader} from "@/components/page-header.tsx";
import {Button} from "@/components/ui/button.tsx";
import {ListRestart} from "lucide-react";


const WorkspaceNamespaceListTab: FC = () => {
    const [uiState] = useUiState()
    const [fetchNamespaces, namespaces] = useNamespaceTree(uiState.workspaceId)

    useEffect(() => {
        fetchNamespaces()
    }, []);

    function handleRefresh() {
        const abortController = new AbortController()
        fetchNamespaces(abortController)
        return (() => abortController.abort())
    }

    if (namespaces == null) return "Loading"
    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                actions={[
                    <Button>
                        <ListRestart onClick={handleRefresh}/>
                        Refresh
                    </Button>
                ]}
                description="Click on a Namespace to select, rename or append. Changes will appear after refresh -> "
            />

            <ol>
                {namespaces && <NamespaceNodeEntry root={namespaces}/>}
            </ol>

        </div>
    )
}

export default WorkspaceNamespaceListTab;


