import React, {FC, useCallback, useEffect, useMemo, useState} from 'react'
import {useUiState} from "@/hook/ui-state.ts";
import {PageHeader} from "@/components/page-header.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";

import {NamespaceList, NamespaceListItem} from "@/types";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {useNamespaceList} from "@/hook";
import {Button} from "@/components/ui/button.tsx";

const WorkspaceNamespaceListTab: FC = () => {
    const [uiState] = useUiState()
    const [listNamespaces, namespaces, isLoading, error] = useNamespaceList()
    const [treePointer, setTreePointer] = useState(uiState.workspaceId)
    const [children, setChildren] = useState<NamespaceList>()

    useEffect(() => {
        const abortController = new AbortController()
        listNamespaces(uiState.workspaceId, abortController)
        return () => {
            abortController.abort()
        }
    }, [uiState.workspaceId]);

    useEffect(() => {
        if (namespaces != null) {
            const c = namespaces.namespaces.filter(v => v.parentId === treePointer).reverse()
            const li = {namespaces: c}
            setChildren(() => li)
        }
    }, [namespaces, treePointer]);

    if (namespaces == null || isLoading) return "Loading..."
    if (error != null) return "Error -"

    function handleClick(id: string) {
        setTreePointer(() => id)
    }

    if (children == null) return "Loading..."

    return (
        <SublistView items={children} callback={handleClick}/>
    )
}

type SublistProps = { items: NamespaceList, callback: (id: string) => void }
const SublistView: FC<SublistProps> = ({items, callback}) => {

    const root = items.namespaces[0]
    const list = items.namespaces.map(namespace =>
        <Card
            className="relative overflow-hidden duration-500 hover:border-primary/50 group"
            key={namespace.id}
            onClick={() => callback(namespace.id)}
        >
            <CardHeader>
                <div className="flex items-center justify-between">
                    <CardTitle>{namespace.name}</CardTitle>
                </div>
            </CardHeader>
            <CardContent>
                <Actions item={namespace}/>
            </CardContent>
        </Card>
    )

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                actions={[
                    <Append appendTo={root.id}/>
                ]}/>
            <p>Current Location: {root.name}</p>
            {list}
        </div>
    )
}

export default WorkspaceNamespaceListTab;
