import {FC} from "react";
import {LucideIcon, Waves, X} from 'lucide-react';


type DummyNode = {
    nodeId: string
    name: string
    tags: string
    description: string
    icon: LucideIcon
}

const dummyNodes: Array<DummyNode> = [
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
}
const NodesLib: FC<Props> = ({onClose}) => {

    console.log("Nodes Dialog")
    return (
        <div
            className={"absolute inset-x-2 inset-y-28 w-64 h-96  bg-white border-2 rounded-lg z-50"}>
            <div className={"flex flex-col items-center justify-center border-amber-500 border-2"}>
                <div className={"flex flex-row w-full p-2 justify-between"}>
                    <div>
                        Nodes
                    </div>
                    <div className={"flex flex-col w-6 h-6  items-center justify-center"}>
                        <button><X size={16} onClick={onClose}/></button>
                    </div>
                </div>
                <div>SearchBar</div>
                <div>TagFilter</div>
                {dummyNodes.map(n =>
                    <NodeCard key={n.nodeId} node={n}/>
                )}
            </div>

        </div>
    )
}

export default NodesLib

type NodeCardProps = {
    node: DummyNode
}
const NodeCard: FC<NodeCardProps> = ({node}) => {
    return (
        <div className={"flex flex-row w-full border-b-2 rounded-lg hover:bg-sky-50 cursor-pointer"}>
            <div>
                <node.icon/>
            </div>
            <div className={"flex flex-col gap-2"} onClick={() => console.log("add node" + node.name)}>
                <div>
                    {node.name}
                </div>
                <div>
                    {node.description}
                </div>
            </div>
        </div>
    )
}