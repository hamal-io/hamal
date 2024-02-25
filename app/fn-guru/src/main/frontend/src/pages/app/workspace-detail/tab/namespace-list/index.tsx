import React, {FC, useCallback, useEffect, useMemo, useState} from 'react'
import {useUiState} from "@/hook/ui-state.ts";
import {PageHeader} from "@/components/page-header.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";

import {NamespaceList} from "@/types";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {useNamespaceList} from "@/hook";

const WorkspaceNamespaceListTab: FC = () => {
    const [uiState] = useUiState()
    const [listNamespaces, namespaces, isLoading, error] = useNamespaceList()
    const [treePointer, setTreePointer] = useState('')

    useEffect(() => {
        const abortController = new AbortController()
        listNamespaces(uiState.workspaceId, abortController)
        setTreePointer(() => uiState.workspaceId)
        return () => {
            abortController.abort()
        }
    }, [uiState.workspaceId]);

    useEffect(() => {
        console.log('TreePointer = ' + treePointer)
    }, [treePointer]);


    if (namespaces == null || isLoading) return "Loading..."
    if (error != null) return "Error -"

    function handleClick(id: string) {
        setTreePointer(() => id)
    }

    const currentRoot = namespaces.namespaces.reverse()[0].id

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                description=""
                actions={[
                    <Append appendTo={currentRoot}/>,
                    // <Actions item={root}/>
                ]}/>

            {namespaces &&
                <ul className="grid grid-cols-1 gap-x-6 gap-y-8">
                    {namespaces.namespaces.map(namespace =>
                        <Card
                            className="relative overflow-hidden duration-500 hover:border-primary/50 group"
                            key={namespace.id}
                            onClick={() => handleClick(namespace.id)}
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
            }

        </div>
    )
}

export default WorkspaceNamespaceListTab;
