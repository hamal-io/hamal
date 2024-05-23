import {FC} from "react";
import {LucideIcon, Waves, X} from 'lucide-react';
import {Input} from "@/components/ui/input.tsx";
import Point from "@/pages/app/nodes-editor/point.ts";
import flowNodes from "@/pages/app/nodes-editor/components/react-flow-nodes.ts";


type DummyNode = {
    nodeId: string
    name: string
    tags: string
    description: string
    icon: LucideIcon
}

const nodeLibrary: Array<DummyNode> = [
    {
        nodeId: "1",
        name: "Node One",
        tags: "null",
        description: "First awesome Node",
        icon: Waves
    },
    {
        nodeId: "2",
        name: "Node Two",
        tags: "null",
        description: "Second awesome Node",
        icon: Waves
    }
]


type Props = {
    onClose: () => void
    addNode: (node) => void
}
const NodesLib: FC<Props> = ({onClose, addNode}) => {

    function handleNodeClicked(libId: string) {
        const node = flowNodes.find(flow => flow.id == libId)
        addNode(node)
    }

    return (
        <div
            className={"absolute inset-x-2 inset-y-28 w-64 h-96  bg-white border-2 rounded-lg z-50"}>
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
                {nodeLibrary.map(n =>
                    <NodeCard key={n.nodeId} node={n} onClick={handleNodeClicked}/>
                )}
            </div>

        </div>
    )
}

export default NodesLib

type NodeCardProps = {
    node: DummyNode
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