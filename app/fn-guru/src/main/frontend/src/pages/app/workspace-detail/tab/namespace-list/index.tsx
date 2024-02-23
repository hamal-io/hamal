import React, {FC, useEffect} from 'react'
import {useNamespaceList} from "@/hook";
import {useUiState} from "@/hook/ui-state.ts";
import SublistView from "@/pages/app/workspace-detail/tab/namespace-list/components/sublistview.tsx";


const WorkspaceNamespaceListTab: FC = () => {
    const [uiState] = useUiState()
    const [listNamespaces, namespaceList, isLoading, error] = useNamespaceList()

    useEffect(() => {
        const abortController = new AbortController()
        listNamespaces(uiState.workspaceId, abortController)
        return () => {
            abortController.abort()
        }
    }, [uiState.workspaceId]);

    if (namespaceList == null || isLoading) return "Loading..."
    if (error != null) return "Error -"

    const root = namespaceList.namespaces.find(i => i.id == uiState.namespaceId)

    return (
        <SublistView parent={root}/>
    )
}


export default WorkspaceNamespaceListTab;
