import {History, LucideIcon, Menu, Play, Plus, Save, Workflow} from 'lucide-react';
import React, {FC} from "react";
import {Button} from "@/components/ui/button.tsx";


const NodesEditorPage = () => {
    return (
        <main className={"flex-col pt-2"}>
            <div className={"p-4"}>
                <div className={"flex flex-row p-2 border rounded-md justify-between h-18 max-w-7xl"}>
                    <div>
                        <MenuItem icon={Menu}/>
                    </div>
                    <div className={"flex flex-row justify-between gap-4"}>
                        <MenuItem icon={Save}/>
                        <MenuItem icon={Play}/>
                    </div>

                </div>
                <div className={"flex flex-col mt-12 p-2 w-16 items-center justify-evenly  gap-4 "}>
                    <MenuItem icon={Plus}/>
                    <MenuItem icon={Workflow}/>
                    <MenuItem icon={History}/>
                </div>
            </div>
            <div className={"absolute bottom-0 left-0 border-2 h-12 w-screen max-w-7xl"}>
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
        <Button className={"w-14 h-14"}>
            <Icon size={24}/>
        </Button>
    )
}