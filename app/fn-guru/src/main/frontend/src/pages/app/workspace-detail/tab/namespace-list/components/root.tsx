import React, {FC} from "react";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {NamespaceListItem} from "@/types";
import {Node} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";

type Props = {
    node: Node<NamespaceListItem>
    changeNode: () => void
}
const NamespaceNodeView: FC<Props> = ({node, changeNode}) => {

    const List = node.descendants.map(node =>
        <Card
            className="relative overflow-hidden duration-500 hover:border-primary/50 group"
            key={node.node.id}
            onClick={changeNode}
        >
            <CardHeader>
                <div className="flex items-center justify-between">
                    <CardTitle>{node.node.name}</CardTitle>
                </div>
            </CardHeader>
            <CardContent>
            </CardContent>
        </Card>
    )

    return (
        <>
            <PageHeader
                title="Namespaces"
                actions={[
                    <Actions item={node.node}/>
                ]}/>
            <p>Current Location: {node.node.name}</p>
            <ul>
                {List}
            </ul>
        </>
    )
}

export default NamespaceNodeView
