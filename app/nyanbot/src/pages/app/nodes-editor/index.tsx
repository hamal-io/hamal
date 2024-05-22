import {History, LucideIcon, Menu, Play, Plus, Save, Workflow} from 'lucide-react';
import React, {FC} from "react";
import {Button} from "@/components/ui/button.tsx";
import ReactFlow, {Background, Controls} from 'reactflow';
import 'reactflow/dist/style.css';


const NodesEditorPage = () => {
    return (
        <main className={"p-2 h-full"}>
            <ReactFlow>
                <Background/>
                <div className={"absolute bottom-12 right-16 z-50"}>
                    <Controls/>
                </div>
                <div className={"absolute inset-x-2 top-4"}>
                    <div
                        className={"flex flex-row p-2  h-18 border rounded-md justify-between bg-white items-center max-w-7xl z-50"}>
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
                        <MenuItem icon={Plus}/>
                        <MenuItem icon={Workflow}/>
                        <MenuItem icon={History}/>
                    </div>
                </div>
            </ReactFlow>
            <div className={"absolute bottom-0 left-0 border-2 h-12 w-screen bg-white max-w-7xl"}>
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

    )
}

export default NodesEditorPage

type ButtonProps = {
    icon: LucideIcon
}
const MenuItem: FC<ButtonProps> = ({icon: Icon}) => {
    return (
        <Button className={"w-14 h-14 bg-white"}>
            <Icon size={24}/>
        </Button>
    )
}