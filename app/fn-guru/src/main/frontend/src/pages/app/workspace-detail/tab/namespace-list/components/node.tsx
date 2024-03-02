import React, {FC} from "react";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {NamespaceListItem} from "@/types";
import {Node} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";

type Props = {
    root: Node<NamespaceListItem>
    changeNode: () => void
}
const NamespaceNodeView: FC<Props> = ({root, changeNode}) => {

    return (
        <ol>
            {root.descendants.map(node =>
                <li key={node.node.id}
                    className={node.isParent() ? "text-blue-600" : undefined}
                >
                    {node.node.name}
                    {/*{node.isParent() ? <NamespaceNodeView root={node} changeNode={changeNode}/> : null}*/}
                </li>)
            }
        </ol>
    )
}



export default NamespaceNodeView


{/*/*  <Card
    className="relative overflow-hidden duration-500 hover:border-primary/50 group"
    onClick={changeNode}
>
    <CardHeader>
        <div className="flex items-center justify-between">
            <CardTitle>{node.node.name}</CardTitle>
        </div>
    </CardHeader>
    <CardContent>
    </CardContent>
</Card>*/
}
