import React from "react";
import {Editor} from "@/components/nodes/editor.tsx";

const DashboardSite = () => {
    return (
        <Editor
            onSave={() => console.log("saved")}
        />
    )
}

export default DashboardSite