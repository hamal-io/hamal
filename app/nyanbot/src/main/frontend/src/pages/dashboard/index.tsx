import React from "react";
import {Editor} from "@/components/nodes/editor.tsx";

const DashboardSite = () => {
    return (
        <Editor
            onSave={(nodes, connections) => console.log(JSON.stringify({
                nodes,
                connections
            }))}
        />
    )
}


export default DashboardSite
