import {FC} from "react";
import {X} from 'lucide-react';
import {Input} from "@/components/ui/input.tsx";
import Point from "@/pages/app/nodes-editor/point.ts";
import NodesLibrary from "@/pages/app/nodes-editor/nodes/library.ts";
import {NodeLibraryEntry} from "@/pages/app/nodes-editor/types.ts";


type Props = {
    onClose: () => void
    onSelect: (id: string) => void
}

const NodesLibraryMenu: FC<Props> = ({onClose, onSelect}) => {
    return (
        <div
            className={"absolute inset-x-2 inset-y-28 w-64   bg-white border-2 rounded-lg z-50"}>
            <div className={"flex flex-col items-center justify-center"}>
                <div className={"flex flex-row p-2 w-full justify-between"}>
                    <div className={"font-bold"}>
                        Nodes
                    </div>
                    <div className={"flex flex-col w-6 h-6 items-center justify-center"}>
                        <button><X size={16} onClick={onClose}/></button>
                    </div>
                </div>
                <div><Input placeholder={"Search Nodes"}/></div>
                <div>TagFilter</div>
                {NodesLibrary.map(n =>
                    <NodeCard key={n.nodeId} node={n} onClick={() => onSelect(n.nodeId)}/>
                )}
            </div>

        </div>
    )
}

export default NodesLibraryMenu

type NodeCardProps = {
    node: NodeLibraryEntry
    onClick: (nodeId: string) => void
    onDrag?: (p: Point, nodeId: string) => void
}
const NodeCard: FC<NodeCardProps> = ({node, onClick, onDrag}) => {

    function handleClick() {
        onClick(node.nodeId)
    }

    return (
        <div className={"flex flex-row p-2 w-full border-b-2 hover:bg-sky-50 cursor-pointer"} onClick={handleClick}>
            <div className={"w-1/5 items-center"}>
                <node.icon/>
            </div>
            <div className={"flex flex-col gap-2"}>
                <div className={"font-bold"}>
                    {node.name}
                </div>
                <div>
                    {node.description}
                </div>
            </div>
        </div>
    )
}