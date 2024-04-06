import React, {FC} from 'react'
import {Canvas} from "@/components/nodes/canvas.tsx";

type EditorProps = {}

export const Editor: FC<EditorProps> = ({}) => {
    return (
        <div className="w-screen h-screen">
            <Canvas
                nodes={[]}
                connections={[]}
                readonly
            />
        </div>
    )
}