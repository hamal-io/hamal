import React, {FC, useEffect, useState} from 'react'
import {useUiState} from "@/hook/ui-state.ts";
import {PageHeader} from "@/components/page-header.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";

import {NamespaceNode} from "@/types";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {useNamespaceList} from "@/hook";
import {Button} from "@/components/ui/button.tsx";

const WorkspaceNamespaceListTab: FC = () => {
    const [uiState] = useUiState()
    const [treePointer, setTreePointer] = useState(uiState.workspaceId)
    const [listNamespaces, namespaceList] = useNamespaceList()
    const [namespaceNode, setNamespaceNode] = useState<NamespaceNode>(null)


    useEffect(() => {
            const abortController = new AbortController()
            listNamespaces(uiState.workspaceId, abortController)
            return (() =>
                    abortController.abort()
            )

        }, [treePointer]
    )

    useEffect(() => {
        if (namespaceList != null) {
            const foundRoot = namespaceList.namespaces.find(item => item.id === treePointer)
            const foundChildren = namespaceList.namespaces.filter(item => item.parentId == treePointer).reverse()
            const res = {root: foundRoot, children: foundChildren}
            setNamespaceNode(() => res)
        }
    }, [namespaceList, treePointer]);


    function changeNode(id: string) {
        setTreePointer(() => id)
    }

    if (namespaceNode == null) return "Loading"

    return (
        <div className="pt-8 px-8">
            <NamespaceRoot node={namespaceNode} changeNode={changeNode}/>
        </div>
    )
}

type NamespaceRootProps = {
    node: NamespaceNode
    changeNode: (id: string) => void
}
const NamespaceRoot: FC<NamespaceRootProps> = ({node, changeNode}) => {

    const list = node.children.map(namespace =>
        <Card
            className="relative overflow-hidden duration-500 hover:border-primary/50 group"
            key={namespace.id}
            onClick={() => changeNode(namespace.id)}
        >
            <CardHeader>
                <div className="flex items-center justify-between">
                    <CardTitle>{namespace.name}</CardTitle>
                </div>
            </CardHeader>
            <CardContent>
            </CardContent>
        </Card>
    )

    return (
        <div>
            <PageHeader
                title="Namespaces"
                actions={[
                    <Button onClick={() => changeNode(node.root.parentId)}>Go up</Button>,
                    <Append appendTo={node.root.id}/>,
                    <Actions item={node.root}/>
                ]}/>
            <p>Current Location: {node.root.name}</p>
            <ul>
                {list}
            </ul>
        </div>
    )
}


export default WorkspaceNamespaceListTab;


