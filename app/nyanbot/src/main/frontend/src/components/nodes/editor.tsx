import React, {FC} from 'react'
import {Canvas} from "@/components/nodes/canvas.tsx";
import ButtonPrimary from "@/components/ui/button/ButtonPrimary.tsx";

type EditorProps = {
    onSave: () => void;
}

export const Editor: FC<EditorProps> = ({onSave}) => {
    return (
        <div style={{background: "whitesmoke"}}>
            <div className="flex flex-row justify-end p-2">
                <ButtonPrimary onClick={onSave}>
                    Save
                </ButtonPrimary>

            </div>
            <div className="h-screen">
                <Canvas
                    nodes={[]}
                    connections={[]}
                    readonly
                />
            </div>
        </div>
    )
}