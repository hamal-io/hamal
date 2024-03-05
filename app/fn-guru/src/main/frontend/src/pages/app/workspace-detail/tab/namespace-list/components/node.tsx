import React, {FC, forwardRef, useMemo, useRef, useState} from "react";
import {NamespaceNode} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";
import {Button} from "@/components/ui/button.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {ChevronDown, ChevronRight} from "lucide-react";


type Props = {
    root: NamespaceNode
}
const NamespaceNodeEntry: FC<Props> = ({root}) => {
    const [expanded, setExpanded] = useState(false)

    const names = root.data.name.split("::")
    const depth = names.length * 2
    const shortName = names[names.length - 1]

    return (
        <li key={0}>
            <span className={'flex align-baseline'}>
                {root.isParent() &&
                    <Button variant={"ghost"} onClick={() => setExpanded(!expanded)}>
                        {expanded ? <ChevronDown size={"16"}/> : <ChevronRight size={"16"}/>}
                    </Button>
                }
                <Actions id={root.data.id} trigger={
                    <Button variant={"ghost"}>{shortName}</Button>
                }/>
            </span>
            {expanded && root.descendants.map((descendant) =>
                <ol key={descendant.data.id} className={`pl-${depth} border-l-2 ml-4`}>
                    <NamespaceNodeEntry root={descendant}/>
                </ol>
            )}

        </li>
    )
}


export default NamespaceNodeEntry
