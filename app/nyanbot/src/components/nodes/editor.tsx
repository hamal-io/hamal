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
    onTest: (state: Any) => void;
}

type EditorContext = {
    state: EditorState,
    dispatch: Dispatch<EditorAction>,
}

export const ContextEditorState = createContext<EditorContext>({
    state: undefined,
    dispatch: undefined
});

export const Editor: FC<EditorProps> = ({nodes, connections, controls, onTest}) => {
    const [state, dispatch] = useReducer(editorReducer, editorInitialState(
        nodes, controls, connections
    ))


    return (
        <ContextEditorState.Provider value={{state, dispatch}}>
            <div className={`h-full ${styles.container}`}>
                <Menu
                    onTest={() => onTest(state)}
                />
                <Canvas
                    nodes={nodes}
                    connections={connections}
                    readonly={false}
                />
            </div>
        </ContextEditorState.Provider>
    )
}
