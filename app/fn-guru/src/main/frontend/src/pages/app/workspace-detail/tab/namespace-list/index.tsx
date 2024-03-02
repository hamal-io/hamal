import React, {FC, useEffect} from 'react'
import {useUiState} from "@/hook/ui-state.ts";
import NamespaceNodeView from "@/pages/app/workspace-detail/tab/namespace-list/components/node.tsx";
import {useNamespaceTree} from "@/pages/app/workspace-detail/tab/namespace-list/components/hook.ts";
import {PageHeader} from "@/components/page-header.tsx";


const WorkspaceNamespaceListTab: FC = () => {
    const [uiState] = useUiState()
    const [fn, result] = useNamespaceTree(uiState.workspaceId)

    console.log("render")

    useEffect(() => {
        fn()
    }, []);

    function onChange() {
        fn()
    }

    if (result == null) return "Loading"
    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                actions={[]}
            />
            <NamespaceNodeView root={result} changeNode={onChange}/>
        </div>
    )
}

export default WorkspaceNamespaceListTab;


