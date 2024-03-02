import React, {FC} from "react";
import {NamespaceNode} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";

type Props = {
    root: NamespaceNode
    changeNode: () => void
}
const NamespaceNodeView: FC<Props> = ({root, changeNode}) => {

    return (
        <ol>
            {root.descendants.map(node =>
                <li key={node.node.id}
                    className={node.isParent() ? "text-blue-600" : undefined}
                >
                    <div>
                    {node.node.name}
                    {node.isParent() && <NamespaceNodeView root={node} changeNode={changeNode}/>}
                    </div>
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
