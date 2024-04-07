import React from "react";
import {Editor} from "@/components/nodes/editor.tsx";

const DashboardSite = () => {
    return (
        <Editor
            onSave={(graph) => console.log(JSON.stringify(graph))}
        />
    )
}


export default DashboardSite
