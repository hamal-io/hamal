import React, {FC, useCallback, useEffect, useMemo, useRef, useState} from 'react'
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
    const [listNamespaces, namespacesList, isLoading, error] = useNamespaceList()
    const [treePointer, setTreePointer] = useState(uiState.workspaceId)
    const [children, setChildren] = useState<NamespaceList>()
    const root = useRef<NamespaceListItem>()

    //TODO228

    if (namespacesList == null || isLoading) return "Loading namespaces..."
    if (children == null) return "Loading children..."
    if (error != null) return "Error -"

    function handleClick(id: string) {
        setTreePointer(() => id)
    }

    return (
        <div className="pt-8 px-8">
            <SublistView root={root.current} items={children} callback={handleClick}/>
        </div>
    )
}


type SublistProps = {
    root: NamespaceListItem,
    items: NamespaceList,
    callback: (id: string) => void
}
const SublistView: FC<SublistProps> = ({root, items, callback}) => {
    if (!items || !root) return "Loading"

    return (
        <div>
            <PageHeader
                title="Namespaces"
                actions={[
                    <Append appendTo={root.id}/>,
                    <Button onClick={() => callback(root.parentId)}>Go up</Button>
                ]}/>
            <p>Current Location: {root.name}</p>
            <ul>
                {items.namespaces.map(namespace =>
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
                )}
            </ul>
        </div>
    )
}

export default WorkspaceNamespaceListTab;
