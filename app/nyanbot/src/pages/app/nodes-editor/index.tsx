import {History, Menu, Play, Plus, Save, Workflow} from 'lucide-react';
import React, {FC} from "react";


const NodesEditorPage = () => {
    return (
        <main className={"flex-col pt-2"}>
            <div className={"p-4"}>
                <div className={"flex flex-row p-2 border rounded-md justify-between h-14"}>
                    <MenuItem icon={Menu}/>
                    <MenuItem icon={Save}/>
                    <MenuItem icon={Play}/>
                </div>
                <div className={"flex flex-col border rounded-md w-14"}>
                    <MenuItem icon={Plus}/>
                    <MenuItem icon={Workflow}/>
                    <MenuItem icon={History}/>
                </div>
            </div>
            <div>Footer</div>
        </main>

    )
}

export default NodesEditorPage

type ButtonProps = {
    icon: React.ComponentType
}
const MenuItem: FC<ButtonProps> = ({icon: Icon}) => {
    return (
        <div className="flex items-center justify-center w-full h-full">
            <Icon size={24}/>
        </div>
    )
}