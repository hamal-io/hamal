import React, {FC, useState} from "react";
import Save from "@/pages/app/flow-detail/pages/func-detail/components/save.tsx";
import Deploy from "@/pages/app/flow-detail/pages/func-detail/components/deploy.tsx";
import History from "@/pages/app/flow-detail/pages/func-detail/components/history.tsx";
import FuncSelector from "@/components/app/func-selector.tsx";
import Editor from "@/components/editor.tsx";

type Props = {
    id: string
    name: string
}
const FuncDetailPage: FC<Props> = ({id}) => {
    const [code, setCode] = useState(`log = require 'log'\nlog.info("That wasn't hard, was it?")`)
    return (
        <div className="h-full ">
            <div className="container flex flex-row justify-between items-center ">
                <FuncSelector
                    className="max-w-[300px]"
                    funcId={id}
                />
                <div className="flex w-full space-x-2 justify-end">
                    <Save/>
                    <Deploy/>
                    <History/>
                </div>
            </div>


            <div className="container h-full py-6">
                <div className="bg-white p-4 rounded-sm border-2">
                    <Editor code={code} onChange={setCode}/>
                </div>
            </div>
        </div>
    )
}

export default FuncDetailPage;