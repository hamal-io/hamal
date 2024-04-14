import React from "react";
import {Editor} from "@/components/nodes/editor.tsx";

export const TestEditorPage = () => {
    return (
        <Editor
            nodes={[]}
            connections={[]}
            onSave={() => {
            }}/>
    )
}