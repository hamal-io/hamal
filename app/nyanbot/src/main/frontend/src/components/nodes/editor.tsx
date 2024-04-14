import React, {createContext, Dispatch, FC, useEffect, useReducer} from 'react'
import {Canvas} from "@/components/nodes/canvas.tsx";
import ButtonPrimary from "@/components/old-ui/button/ButtonPrimary.tsx";
import {Connection, ControlId, ControlType, Graph, Node, NodeId, NodeType} from '@/components/nodes/types';

type EditorProps = {
    nodes: Node[];
    connections: Connection[];
    onSave: (graph: Graph) => void;
}

export type StateNode = {
    id: NodeId;
    type: NodeType;
}

export type StateControl = {
    id: ControlId;
    type: ControlType;
    nodeId: NodeId;
    defaultValue: string;
}

const initialState = (nodes: Node[], connections: Connection[]): State => {
    return {
        nodes: nodes.reduce((acc, cur) => {
            return {
                ...acc, [cur.id]: {
                    id: cur.id,
                    type: cur.type
                }
            }
        }, {}),
        controls: nodes.reduce((nodeAcc, nodeCur) => {
            const controls = nodeCur.controls.reduce((controlAcc, controlCur) => {
                return {
                    ...controlAcc, [controlCur.id]: {
                        id: controlCur.id,
                        type: controlCur.type,
                        nodeId: controlCur.id,
                        defaultValue: ''
                    }
                };
            }, {});
            return {...nodeAcc, ...controls}
        }, {})
    }
}

export interface State {
    nodes: { [id: NodeId]: StateNode };
    controls: { [id: ControlId]: StateControl };
}

export type Action =
    | { type: "CONTROL_TEXT_UPDATED"; id: ControlId, value: string }


export type EditorState = {
    state: State,
    dispatch: Dispatch<Action>
}

export const ContextEditorState = createContext<EditorState>({
    state: {nodes: {}, controls: {}},
    dispatch: undefined
})

export const Editor: FC<EditorProps> = ({nodes, connections, onSave}) => {

    const [state, dispatch] = useReducer(reducer, initialState(nodes, connections))
    useEffect(() => {
        console.log(state)
    }, [state]);

    return (
        <ContextEditorState.Provider value={{state, dispatch}}>
            <div style={{background: "whitesmoke"}}>
                <div className="flex flex-row justify-end p-2">
                    <ButtonPrimary onClick={() => {
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
            state.controls[action.id].defaultValue = action.value;
            return {...state}
        default:
            throw new Error();
    }
}