import React, {FC, useState} from "react";
import {NamespaceNode} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";
import {Button} from "@/components/ui/button.tsx";

type Props = {
    root: NamespaceNode
    changeNode: () => void
}
const NamespaceNodeView: FC<Props> = ({root, changeNode}) => {
    const [expanded, setExpanded] = useState(false)

    return (
        <ol>
            {root.isParent() ?
                <li key={root.data.id} className={"text-blue-600"}>
                    <div>
                        <Button variant={"secondary"} onClick={() => setExpanded(!expanded)}>+</Button>
                        {root.data.name}
                        {expanded && root.descendants.map(desc =>
                            <NamespaceNodeView root={desc} changeNode={changeNode}/>
                        )}
                    </div>

                </li>

                : <li key={root.data.id}>{root.data.name}</li>}
        </ol>

    )
}




export default NamespaceNodeView




{/*  <Card
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
</Card>*/}
