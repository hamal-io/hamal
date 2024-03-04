import React, {FC, useEffect} from 'react'
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


    if (namespaces == null) return "Loading"
    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                actions={[
                    <Button>
                        <ListRestart onClick={() => fetchNamespaces()}/>
                        Refresh
                    </Button>
                ]}
            />

            <ol>
                <NamespaceNodeEntry root={namespaces}/>
            </ol>


        </div>
    )
}

export default WorkspaceNamespaceListTab;


