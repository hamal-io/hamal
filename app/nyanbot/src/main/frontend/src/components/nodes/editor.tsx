import React, {createContext, Dispatch, FC, useEffect, useReducer} from 'react'
import {Canvas} from "@/components/nodes/canvas.tsx";
import ButtonPrimary from "@/components/old-ui/button/ButtonPrimary.tsx";
import {Connection, ConnectionId, Control, ControlId, Graph, Node, NodeId} from '@/components/nodes/types';

type EditorProps = {
    nodes: Node[];
    connections: Connection[];
    controls: Control[];
    onSave: (graph: Graph) => void;
}

const initialState = (nodes: Node[], connections: Connection[], controls: Control[]): State => {
    return {
        nodes: nodes.reduce((acc, cur) => {
            return {...acc, [cur.id]: cur}
        }, {}),
        connections: connections.reduce((acc, cur) => {
            return {...acc, [cur.id]: cur}
        }, {}),
        controls: controls.reduce((acc, cur) => {
            return {...acc, [cur.id]: cur};
        }, {}),
        nodeControlIds: controls.reduce((acc, cur) => {
            acc[cur.nodeId] = acc[cur.nodeId] || []
            acc[cur.nodeId].push(cur.id)
            return acc
        }, {}),
    }
}

export interface State {
    nodes: { [id: NodeId]: Node };
    connections: { [id: ConnectionId]: Connection };
    controls: { [id: ControlId]: Control };
    nodeControlIds: { [id: NodeId]: ControlId[] }
}

export type Action =
    | { type: "CONTROL_TEXT_UPDATED"; id: ControlId, value: string }


export type EditorState = {
    state: State,
    dispatch: Dispatch<Action>
}

export const ContextEditorState = createContext<EditorState>({
    state: {nodes: {}, connections: {}, controls: {}, nodeControlIds: {}},
    dispatch: undefined
})

export const Editor: FC<EditorProps> = ({nodes, connections, controls, onSave}) => {

    const [state, dispatch] = useReducer(reducer, initialState(nodes, connections, controls))
    useEffect(() => {
        console.log(state)
    }, [state]);

    return (
        <ContextEditorState.Provider value={{state, dispatch}}>
            <div style={{background: "whitesmoke"}}>
                <div className="flex flex-row justify-end p-2">
                    <ButtonPrimary onClick={() => {
                        onSave({
                            nodes: Object.entries(state.nodes).map(([_, value]) => value),
                            connections: Object.entries(state.connections).map(([_, value]) => value),
                            controls: Object.entries(state.controls).map(([_, value]) => value)
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

const reducer = (state: State, action: Action): State => {
    switch (action.type) {
        case 'CONTROL_TEXT_UPDATED':
            // FIXME
            state.controls[action.id].defaultValue = action.value;
            return {...state}
        default:
            throw new Error();
    }
}