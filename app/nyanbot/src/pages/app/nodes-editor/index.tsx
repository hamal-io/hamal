import {History, Menu, Play, Plus, Save, Workflow} from 'lucide-react';
import React, {useState} from "react";
import ReactFlow, {Background, Controls} from 'reactflow';
import 'reactflow/dist/style.css';
import MenuItem from "@/pages/app/nodes-editor/components/menu-item.tsx";
import NodesLib from "@/pages/app/nodes-editor/components/nodes-lib.tsx";


const NodesEditorPage = () => {
    const [libOpen, setLibOpen] = useState(false)



    return (
        <>
            <main className={"p-2 h-full"}>
                <ReactFlow>
                    <Background/>
                    <div className={"absolute bottom-12 right-16 z-50"}>
                        <Controls/>
                    </div>
                </ReactFlow>
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

            {libOpen === true ? <NodesLib onClose={() => setLibOpen(false)}/> : null}

        </>

    )
}

export default NodesEditorPage

