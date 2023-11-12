import React, {FC, useEffect, useState} from "react";
import Save from "@/pages/app/flow-detail/pages/func-detail/components/save.tsx";
import History from "@/pages/app/flow-detail/pages/func-detail/components/history.tsx";
import FuncSelector from "@/components/app/func-selector.tsx";
import Editor from "@/components/editor.tsx";
import {useFuncGet} from "@/hook/api/func.ts";
import {useParams} from "react-router-dom";
import Actions from "@/pages/app/flow-detail/pages/func-detail/components/actions.tsx";

type Props = {}
const FuncDetailPage: FC<Props> = ({}) => {
    const {funcId} = useParams()
    const [func, funcLoading, funcError] = useFuncGet(funcId)

    const [name, setName] = useState('')
    const [code, setCode] = useState('')

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
                />
                <div className="flex w-full space-x-2 justify-end">
                    <Save funcId={funcId} code={code} name={name}/>
                    <History/>
                    <Actions funcId={funcId}/>
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