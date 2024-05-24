import {History, Menu, Play, Plus, Save, Workflow} from 'lucide-react';
import React, {useRef, useState} from "react";

import 'reactflow/dist/style.css';
import MenuItem from "@/pages/app/nodes-editor/components/menu-item.tsx";
import NodesLibraryMenu from "@/pages/app/nodes-editor/components/nodes-library-menu.tsx";
import {buildNode} from "@/pages/app/nodes-editor/nodes/builder.tsx";
import NodeEditor from "@/pages/app/nodes-editor/components/editor.tsx";


const NodesEditorPage = () => {
    const [libOpen, setLibOpen] = useState(false)
    const editorRef = useRef(null)

    function onLibraryNodeSelected(id: string) {
        editorRef.current.add(buildNode(id))
    }


    return (
        <>
            <main className={"p-4 h-full"}>
                <NodeEditor ref={editorRef}/>
                <div className={"absolute inset-x-2 top-4"}>
                    <div
                        className={"flex flex-row p-2 h-18 border rounded-md justify-between bg-white items-center max-w-7xl z-40"}>
                        <div>
                            <MenuItem icon={Menu}/>
                        </div>
                        <div className={"flex flex-row justify-between gap-4"}>
                            <MenuItem icon={Save}/>
                            <MenuItem icon={Play}/>
                        </div>
                    </div>
                </div>

                <div className={"absolute inset-x-2 inset-y-14 w-16 h-16"}>
                    <div className={"flex flex-col mt-12 p-2 w-16  items-center justify-evenly gap-4 z-50"}>
                        <MenuItem onClick={() => setLibOpen(true)} icon={Plus}/>
                        <MenuItem icon={Workflow}/>
                        <MenuItem icon={History}/>
                    </div>
                </div>

                <div className={"absolute bottom-0 left-0 border-2 h-12 w-screen bg-white"}>
                    <div className={"flex flex-row gap-4 justify-items-start"}>
                        <div>
                            Flow 1
                        </div>
                        <div>
                            Flow 2
                        </div>
                    </div>
                </div>
            </main>

            {libOpen === true ?
                <NodesLibraryMenu onClose={() => setLibOpen(false)} onSelect={onLibraryNodeSelected}/> : null}

        </>

    )
}

export default NodesEditorPage

