import React, {FC, useState} from "react";
import {NamespaceNode} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";
import {Button} from "@/components/ui/button.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {ChevronDown, ChevronRight, Dot} from "lucide-react";


type Props = {
    root: NamespaceNode
}
const NamespaceNodeEntry: FC<Props> = ({root}) => {
    const [expanded, setExpanded] = useState(false)

    const names = root.data.name.split("::")
    const shortName = names[names.length - 1]

    return (
        <li key={root.data.id}>
            <div className={'flex align-baseline'}>
                {root.isParent() ?
                    <Button variant={"ghost"} onClick={() => setExpanded(!expanded)} className={"p-1"}>
                        {expanded ? <ChevronDown size={"16"}/> : <ChevronRight size={"16"}/>}
                    </Button>
                            :
                    <Button variant={"ghost"} className={"p-1"}>
                        <Dot size={"16"}/>
                    </Button>
                }
                <Actions id={root.data.id} trigger={
                    <Button variant={"ghost"} className={"p-1"}>{shortName}</Button>
                }/>
            </div>
            {expanded && root.descendants.map((descendant) =>
                <ol key={descendant.data.id} className={`ml-2 border-l-2`}>
                    <NamespaceNodeEntry root={descendant} />
                </ol>
            )}

        </li>
    )
}


export default NamespaceNodeEntry
