import React, {FC, useCallback, useEffect, useMemo, useRef, useState} from 'react'
import {useUiState} from "@/hook/ui-state.ts";
import {PageHeader} from "@/components/page-header.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";

import {NamespaceList, NamespaceListItem, NamespaceView} from "@/types";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {useNamespaceList} from "@/hook";
import {Button} from "@/components/ui/button.tsx";
import {Pocket} from "lucide-react";

const WorkspaceNamespaceListTab: FC = () => {
    const [uiState] = useUiState()
    const [treePointer, setTreePointer] = useState(uiState.workspaceId)
    const [listNamespaces, namespaceList, listLoading, listError] = useNamespaceList()
    const [listView, setListView] = useState<NamespaceView>(null)

    useEffect(() => {
        const abortController = new AbortController()
        listNamespaces(uiState.workspaceId, abortController)
        return (() =>
                abortController.abort()
        )
    }, [treePointer]);

    useEffect(() => {
        if (namespaceList != null) {
            const foundRoot = namespaceList.namespaces.find(item => item.id === treePointer)
            const foundChildren = namespaceList.namespaces.filter(item => item.parentId == treePointer)
            const res = {root: foundRoot, children: foundChildren}
            setListView(() => res)
        }
    }, [namespaceList]);


    function handleClick(id: string) {
        setTreePointer(() => id)

    }

    if (listView == null) return "Loading"

    return (
        <div className="pt-8 px-8">
            <SublistView view={listView} callback={handleClick}/>
        </div>
    )
}

type SublistProps = {
    view: NamespaceView
    callback: (id: string) => void
}
const SublistView: FC<SublistProps> = ({view, callback}) => {
    return (
        <div>
            <PageHeader
                title="Namespaces"
                actions={[
                    <Append appendTo={view.root.id}/>,
                    <Button onClick={() => callback(view.root.parentId)}>Go up</Button>
                ]}/>
            <p>Current Location: {view.root.name}</p>
            <ul>
                {view.children.map(namespace =>
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


