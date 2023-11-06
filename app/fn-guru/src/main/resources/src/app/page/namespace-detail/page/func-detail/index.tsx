import React, {useEffect, useState} from 'react'
import {useParams} from "react-router-dom";
import {useApiDeployLatestCode, useApiGet, useApiPatch} from "../../../../../hook";
import {ApiFunc} from "../../../../../api/types";
import Editor from "../../../../../component/editor";
import {Button, TextInput} from "flowbite-react";

interface ApiFuncUpdateSubmitted {
    id: string;
    status: string;
    funcId: string;
}


const NamespaceFuncDetailPage: React.FC = () => {
    const {funcId} = useParams()

    const [func, isLoading, error] = useApiGet<ApiFunc>(`v1/funcs/${funcId}`)
    const [code, setCode] = useState("")
    const [name, setName] = useState("")

    const [updateFunc] = useApiPatch<ApiFuncUpdateSubmitted>()
    const [deployFunc] = useApiDeployLatestCode()

    useEffect(() => {
        if (func != null) {
            setName(func.name)
            setCode(func.code.current.value)
        }
    }, [func]);


    if (isLoading) {
        return (<p>Loading</p>)
    }


    return (
        <main className="flex-1 w-full mx-auto text-lg h-full bg-gray-200">
            <div className="w-3/4 space-y-6">
                <div>
                    <TextInput
                        id="name"
                        placeholder="Useful function name..."
                        required
                        value={name}
                        onChange={evt => setName(evt.target.value)}
                    />
                </div>

            </div>

            <div className="flex flex-col items-start py-4">
                <div className="w-3/4">
                    <Editor
                        code={code}
                        onChange={code => {
                            setCode(code || "")
                        }}
                    />
                </div>

                <div className="flex flex-row ">
                    <Button onClick={() => {
                        updateFunc(`v1/funcs/${funcId}`, {
                            name,
                            code
                        })
                    }}> Save </Button>

                    <Button onClick={() => {
                        deployFunc(funcId)
                    }}> Deploy Func </Button>
                </div>

            </div>
        </main>

    );
}


export default NamespaceFuncDetailPage

