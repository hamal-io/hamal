import React, {FC, useContext, useEffect, useState} from "react";
import Save from "@/pages/app/func-detail/components/save.tsx";
import History from "@/pages/app/func-detail/components/history.tsx";
import Editor from "@/components/editor.tsx";
import {useParams} from "react-router-dom";
import Actions from "@/pages/app/func-detail/components/actions.tsx";
import {useFuncGet} from "@/hook/func.ts";
import Deploy from "@/pages/app/func-detail/components/deploy.tsx";
import WorkspaceFuncSelector from "@/pages/app/func-detail/components/workspace-func-selector.tsx";

type Props = {}
const FuncDetailPage: FC<Props> = ({}) => {
    const {funcId} = useParams()
    // const [func, funcLoading, funcError] = useFuncGet(funcId)
    const [getFunc, func, funcLoading, funcError] = useFuncGet()
    const [name, setName] = useState('')
    const [code, setCode] = useState('')

    useEffect(() => {
        const abortController = new AbortController()
        getFunc(funcId, abortController)
        return () => {
            abortController.abort()
        }
    }, [funcId]);

    useEffect(() => {
        if (func != null) {
            setName(func.name)
            setCode(func.code.value)
        }
    }, [func]);

    if (func == null || funcLoading) return "Loading..."
    return (
        <div className="h-full ">
            <div className="container flex flex-row justify-between items-center ">
                <WorkspaceFuncSelector
                    className="max-w-[300px]"
                    funcId={funcId}
                />
                <div className="flex w-full space-x-2 justify-end">
                    <Deploy funcId={funcId} code={code}/>
                    <Save funcId={funcId} code={code} name={name}/>
                    <History funcId={funcId}/>
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