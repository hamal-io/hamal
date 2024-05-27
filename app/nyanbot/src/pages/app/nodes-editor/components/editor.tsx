import ReactFlow, {
    addEdge,
    applyEdgeChanges,
    applyNodeChanges,
    Background,
    Connection,
    Controls,
    EdgeChange,
    Node,
    NodeChange,
    useEdgesState,
    useNodesState
} from "reactflow";
import React, {forwardRef, useCallback, useImperativeHandle, useMemo} from "react";
import TextInputNode from "@/pages/app/nodes-editor/nodes/text-input.tsx";
import TelegramSenderNode from "@/pages/app/nodes-editor/nodes/telegram-sender.tsx";
import FilterNode from "@/pages/app/nodes-editor/nodes/filter-node.tsx";
import NumberInputNode from "@/pages/app/nodes-editor/nodes/number-input.tsx";
import TextBuilderNode from "@/pages/app/nodes-editor/nodes/text-builder.tsx";

interface Handles {
    add: (node: Node) => void
}

type Props = {}
const NodeEditor = forwardRef<Handles, Props>(({}, ref) => {
    const [nodes, setNodes] = useNodesState([]);
    const [edges, setEdges] = useEdgesState([]);

    const nodeTypes = useMemo(() => {
        return {
            textInput: TextInputNode,
            telegramSender: TelegramSenderNode,
            filter: FilterNode,
            numberInput: NumberInputNode,
            textBuilder: TextBuilderNode
        }
    }, [])

    const onNodesChange = useCallback(
        (changes: NodeChange[]) => {
            console.log(changes);
            setNodes((nds) => applyNodeChanges(changes, nds))
        },
        [],
    );
    const onEdgesChange = useCallback(
        (changes: EdgeChange[]) => {
            //console.log(changes);
            setEdges((eds) => applyEdgeChanges(changes, eds))
        },
        [],
    );

    const onConnect = useCallback(
        (params: Connection) => {
            if (params.targetHandle == 'union') {
                setEdges((eds) => addEdge(params, eds))
            } else if (params.sourceHandle == params.targetHandle) {
                setEdges((eds) => addEdge(params, eds))
            } else {
                //give some feedback
            }
        },
        [],
    );

    useImperativeHandle(ref, () => ({
        add(node: Node) {
            let editorNode = node
            if (nodes.find((n) => n.id == node.id)) {
                editorNode = {...node, id: `${node.id + Math.random()}`} //Fixme
            }
            setNodes(prevState => [...prevState, editorNode]);
        }
    }));

    return (
        <ReactFlow nodes={nodes}
                   nodeTypes={nodeTypes}
                   onNodesChange={onNodesChange}
                   edges={edges}
                   onEdgesChange={onEdgesChange}
                   onConnect={onConnect}
                   fitView
        >
            <Background/>
            <div className={"absolute bottom-12 right-16 z-50"}>
                <Controls/>
            </div>
        </ReactFlow>
    )
})

export default NodeEditor