import React, {FC, useEffect, useState} from 'react'
import {useNavigate, useParams} from "react-router-dom";
import Editor from "../../../../../component/editor";
import {Button, Card} from "flowbite-react";
import {ApiExecSimple, ApiFunc} from "../../../../../api/types";
import {getFunction, invokeAdhoc, invokeFunc, listExecs, updateFunc} from "../../../../../api";

const NmaespaceFuncDetailPage: React.FC = () => {
    const {funcId} = useParams()

    const navigate = useNavigate()
    const [func, setFunc] = useState<ApiFunc>({} as ApiFunc)

    const [name, setName] = useState<string | undefined>()
    const [code, setCode] = useState('')
    const [loading, setLoading] = useState(false);


    useEffect(() => {
        if (funcId) {
            setLoading(true)
            getFunction(funcId).then(response => {
                setFunc((response))
                setCode(response.code.value)
                setName(response.name)
                setLoading(false)
            })
        }
    }, [funcId]);


    return (
        <div className="flex-1  w-full mx-auto h-full bg-gray-100">

            <div className="flex flex-col items-center justify-center">
                <div className="w-full">
                    <Editor
                        code={code}
                        onChange={code => setCode(code)}
                    />
                </div>

                <div className="flex flex-row ">
                    <Button onClick={() => {

                        invokeAdhoc({code}).then(response => {
                            console.log(response)
                        })

                    }}>Test</Button>

                    <Button onClick={() => {
                        updateFunc(funcId, {name: null, code: code}).then(response => {
                            console.log(response)
                        })
                    }}> Save </Button>

                </div>

            </div>
        </div>

    );
}


export default NmaespaceFuncDetailPage

