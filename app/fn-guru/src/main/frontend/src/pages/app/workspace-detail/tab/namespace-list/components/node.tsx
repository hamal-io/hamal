import React, {FC, useState} from "react";
import {NamespaceNode} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";
import {Button} from "@/components/ui/button.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {ChevronDown, ChevronRight} from "lucide-react";


type Props = {
    root: NamespaceNode
}
const NamespaceNodeEntry: FC<Props> = ({root}) => {
    const [expanded, setExpanded] = useState(false)

    return (
        <li>
            <span
                style={{
                    display: 'flex',
                    alignItems: 'center'
                }}>
                {root.isParent() &&
                    <Button variant={"secondary"} onClick={() => setExpanded(!expanded)}>
                        {expanded ? <ChevronDown/> : <ChevronRight/>}
                    </Button>
                }
                <Actions item={root.data}/>
            </span>
            {expanded && root.descendants.map(descendant =>
                <ol>
                    <NamespaceNodeEntry root={descendant}/>
                </ol>
            )}
        </li>
    )
}


export default NamespaceNodeEntry
