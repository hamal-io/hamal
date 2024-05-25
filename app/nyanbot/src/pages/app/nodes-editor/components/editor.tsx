import ReactFlow, {
    addEdge,
    applyEdgeChanges,
    applyNodeChanges,
    Background,
    Controls,
    Node,
    useNodesState
} from "reactflow";
import React, {forwardRef, useCallback, useImperativeHandle, useState} from "react";
import {nodeTypes} from "@/pages/app/nodes-editor/nodes/registry.ts";

interface Handles {
    add: (node: Node) => void
}

type Props = {}
const NodeEditor = forwardRef<Handles, Props>(({}, ref) => {
    const [nodes, setNodes] = useNodesState([]);
    const [edges, setEdges] = useState([]);

    const onNodesChange = useCallback(
        (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [],
    );
    const onEdgesChange = useCallback(
        (changes) => setEdges((eds) => applyEdgeChanges(changes, eds)),
        [],
    );

    const onConnect = useCallback(
        (params) => {
            if (params.sourceHandle == params.targetHandle) {
                setEdges((eds) => addEdge(params, eds))
            }
        },
        [],
    );

    useImperativeHandle(ref, () => ({
        add(node: Node) {
            setNodes(prevState => [...prevState, node]);
        }
    }));

    return (
        <ReactFlow nodes={nodes}
                   nodeTypes={nodeTypes}
                   onNodesChange={onNodesChange}
                   edges={edges}
                   onEdgesChange={onEdgesChange}
                   onConnect={onConnect}
                   fitView>
            <Background/>
            <div className={"absolute bottom-12 right-16 z-50"}>
                <Controls/>
            </div>
        </ReactFlow>
    )
})

export default NodeEditor