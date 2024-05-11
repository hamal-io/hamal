import React, {createContext, Dispatch, FC, useReducer} from 'react'
import styles from "./editor.module.css";
import {Canvas} from "@/components/nodes/canvas.tsx";
import {Connection, Control, Graph, Node} from '@/components/nodes/types';
import {EditorAction, editorInitialState, editorReducer, EditorState} from "@/components/nodes/state.ts";
import {Menu} from "@/components/nodes/menu.tsx";

type EditorProps = {
    nodes: Node[];
    connections: Connection[];
    controls: Control[];
    onSave: (graph: Graph) => void;
}

type EditorContext = {
    state: EditorState,
    dispatch: Dispatch<EditorAction>,
}

export const ContextEditorState = createContext<EditorContext>({
    state: undefined,
    dispatch: undefined
});

export const Editor: FC<EditorProps> = ({nodes, connections, controls, onSave}) => {
    const [state, dispatch] = useReducer(editorReducer, editorInitialState(
        nodes, controls, connections
    ))

    return (
        <ContextEditorState.Provider value={{state, dispatch}}>
            {/*<div style={{background: "whitesmoke"}}>*/}
            {/*<div className="flex flex-row justify-end p-2">*/}
            {/*    <ButtonPrimary onClick={() => {*/}
            {/*        onSave({*/}
            {/*            connections: Object.entries(state.connectionState.connections).map(([_, value]) => value),*/}
            {/*            controls: Object.entries(state.controlState.controls).map(([_, value]) => value),*/}
            {/*            nodes: Object.entries(state.nodeState.nodes).map(([_, value]) => value)*/}
            {/*        })*/}
            {/*    }}>*/}
            {/*        Save*/}
            {/*    </ButtonPrimary>*/}

            <div className={styles.container}>
                <Menu/>
                <Canvas
                    nodes={nodes}
                    connections={connections}
                    readonly={false}

                />
            </div>
        </ContextEditorState.Provider>
    )
}
