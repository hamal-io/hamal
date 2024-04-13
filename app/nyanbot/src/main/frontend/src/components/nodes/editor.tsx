import React, {FC} from 'react'
import {Canvas} from "@/components/nodes/canvas.tsx";
import ButtonPrimary from "@/components/old-ui/button/ButtonPrimary.tsx";
import {Connection, ControlCondition, ControlInput, ControlText, Graph, Node} from '@/components/nodes/types';

type EditorProps = {
    nodes: Node[];
    connections: Connection[];
    onSave: (graph: Graph) => void;
}

export const Editor: FC<EditorProps> = ({nodes, connections, onSave}) => {
    return (
        <div style={{background: "whitesmoke"}}>
            <div className="flex flex-row justify-end p-2">
                <ButtonPrimary onClick={() => {
                    onSave({nodes, connections})
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
    )
}