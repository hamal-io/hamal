import React, {FC, useContext, useEffect, useState} from "react";
import Save from "@/pages/app/flow-detail/pages/func-detail/components/save.tsx";
import History from "@/pages/app/flow-detail/pages/func-detail/components/history.tsx";
import FuncSelector from "@/pages/app/flow-detail/pages/func-detail/components/func-selector.tsx";
import Editor from "@/components/editor.tsx";
import {useParams} from "react-router-dom";
import Actions from "@/pages/app/flow-detail/pages/func-detail/components/actions.tsx";
import {FlowContext} from "@/pages/app/flow-detail";
import {useFuncGet} from "@/hook/func.ts";

type Props = {}
const FuncDetailPage: FC<Props> = ({}) => {
    const flow = useContext(FlowContext)
    const {funcId} = useParams()
    // const [func, funcLoading, funcError] = useFuncGet(funcId)
    const [getFunc, func, funcLoading, funcError] = useFuncGet()

    const [name, setName] = useState('')
    const [code, setCode] = useState('')

    useEffect(() => {
        getFunc(funcId)
    }, [funcId]);

    useEffect(() => {
        if (func != null) {
            setName(func.name)
            setCode(func.code.current.value)
        }
    }, [func]);

    if (funcLoading) return "Loading..."
    return (
        <div className="h-full ">
            <div className="container flex flex-row justify-between items-center ">
                <FuncSelector
                    className="max-w-[300px]"
                    funcId={funcId}
                    flowId={flow.id}
                />
                <div className="flex w-full space-x-2 justify-end">
                    <Save funcId={funcId} code={code} name={name}/>
                    <History/>
                    <Actions funcId={funcId} code={code}/>
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