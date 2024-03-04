import React, {FC, useState} from "react";
import {NamespaceNode} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";
import {Button} from "@/components/ui/button.tsx";
import {ContextMenu} from "@/components/ui/context-menu.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {Textarea} from "@/components/ui/textarea.tsx";
import append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";
import {ChevronDown, ChevronRight, Plus} from "lucide-react";


type Props = {
    root: NamespaceNode
    changeNode: () => void
}
const NamespaceNodeView: FC<Props> = ({root, changeNode}) => {
    const [expanded, setExpanded] = useState(false)

    const Parent = () => {
        return (
            <li key={root.data.id}>
                <div>
                    <Entry/>
                    {expanded && root.descendants.map(desc =>
                        <NamespaceNodeView root={desc} changeNode={changeNode}/>
                    )}
                </div>

            </li>
        )
    }

    const Single = () => {
        return (
            <li key={root.data.id}>
                <Entry/>
            </li>
        )
    }


    const Entry = () => {


        return (
            <div style={{
                display: 'flex',
                alignItems: 'center'
            }}>
                {root.isParent() &&
                    <Button onClick={() => setExpanded(!expanded)}>
                        {expanded ? <ChevronDown/> : <ChevronRight/>}
                    </Button>
                }
                {stripName(root.data.name)}
                <Actions item={root.data}/>
            </div>
        )
    }

    return (
        <ol>
            {root.isParent() ? <Parent/> : <Single/>}
        </ol>

    )
}

function stripName(longName: string) {
    const names = longName.split("::")
    return names[names.length - 1]
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
</Card>
    <Button variant={"secondary"} onClick={() => setExpanded(!expanded)}>
                <p className={root.isParent() ? "text-blue-600" : undefined}>{root.data.name}</p>
    </Button>


*/
}
