import React, {createContext, FC} from 'react'
import {Canvas} from "@/components/nodes/canvas.tsx";
import ButtonPrimary from "@/components/old-ui/button/ButtonPrimary.tsx";
import {Connection, ConnectionId, Control, ControlId, Graph, Node, NodeId} from '@/components/nodes/types';
import {Action, useState} from "@/components/nodes/state";

type EditorProps = {
    nodes: Node[];
    connections: Connection[];
    controls: Control[];
    onSave: (graph: Graph) => void;
}

export type EditorState = {
    nodes: { [id: NodeId]: Node };
    connections: { [id: ConnectionId]: Connection };
    controls: { [id: ControlId]: Control };
    nodeControlIds: { [id: NodeId]: ControlId[] };
    dispatch: (action: Action) => void;
}

export const ContextEditorState = createContext<EditorState>({
    nodes: {},
    connections: {},
    controls: {},
    nodeControlIds: {},
    dispatch: (Action) => {
    }
})

export const Editor: FC<EditorProps> = ({nodes, connections, controls, onSave}) => {
    const [state, dispatch] = useState(connections, controls, nodes)
    return (
        <ContextEditorState.Provider value={{
            nodes: state.nodeState.nodes,
            connections: state.connectionState.connections,
            controls: state.controlState.controls,
            nodeControlIds: state.controlState.nodeControlIds,
            dispatch
        }}>
            <div style={{background: "whitesmoke"}}>
                <div className="flex flex-row justify-end p-2">
                    <ButtonPrimary onClick={() => {
                        onSave({
                            connections: Object.entries(state.connectionState.connections).map(([_, value]) => value),
                            controls: Object.entries(state.controlState.controls).map(([_, value]) => value),
                            nodes: Object.entries(state.nodeState.nodes).map(([_, value]) => value)
                        })
                    }}>
                        Save
                    </ButtonPrimary>

                </div>
                <div className="h-screen">
                    <Canvas
                        nodes={nodes}
                        connections={connections}
                        readonly
                    />
                </div>
            </div>
        </ContextEditorState.Provider>
    )
}
