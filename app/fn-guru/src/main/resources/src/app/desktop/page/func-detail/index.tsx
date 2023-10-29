import React, {FC, useEffect, useState} from 'react'
import {useNavigate, useParams} from "react-router-dom";
import Editor from "../../../../component/editor";
import {Button, Card} from "flowbite-react";
import {ApiExecSimple, ApiFunc} from "../../../../api/types";
import {getFunction, invokeAdhoc, invokeFunc, listExecs, updateFunc} from "../../../../api";

type TabType = 'Dashboard' | 'Code' | 'Runs' | 'Settings'


interface EditorTabProps {
    code: string;
    funcId: string;
    setCode: (code: string) => void;
}

const EditorTab: FC<EditorTabProps> = (props) => {
    return (
        <div className="flex flex-col p-10 items-center justify-center">
            <div className="w-full">
                <Editor
                    code={props.code}
                    onChange={code => props.setCode(code)}
                />
            </div>

            <div className="flex flex-row ">
                <Button onClick={() => {

                    invokeAdhoc({
                        code: props.code
                    }).then(response => {
                        console.log(response)
                    })

                }}>Test</Button>

                <Button onClick={() => {
                    updateFunc(props.funcId, {name: null, code: props.code}).then(response => {
                        console.log(response)
                    })
                }}> Save  </Button>

            </div>

        </div>
    )
}

interface RunTabProps {
    funcId: string;
}

const RunTab: FC<RunTabProps> = (props) => {
    const [loading, setLoading] = useState(false);
    const [execs, setExecs] = useState<ApiExecSimple[]>([])

    useEffect(() => {
        setLoading(true)
        listExecs({funcId: props.funcId, limit: 10}).then(response => {
            setExecs(response.execs)
            setLoading(false)
        })
    }, [props.funcId]);

    const exec = execs.map(exec => (
        <Card
            key={exec.id}
            className="m-8 max-w-sm"
        >
            <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                <p>{exec.id} : {exec.status}</p>
            </h5>
        </Card>
    ))


    return (
        <div className="flex flex-col">
            <Button onClick={() => {
                invokeFunc(props.funcId, {}).then(response => {
                    console.log(response)

                    setLoading(true)
                    listExecs({funcId: props.funcId, limit: 10}).then(response => {
                        setExecs(response.execs)
                        setLoading(false)
                    })
                })
            }}> Run Function </Button>
            <div className="flex flex-row">
                <div className="flex flex-col items-start w-1/4">

                    <Card>
                        <p> Trigger Placeholder </p>
                    </Card>

                    <Card>
                        <p> Trigger Placeholder </p>
                    </Card>

                    <Card>
                        <p> New Trigger </p>
                        <Button onClick={() => {
                        }}> Add </Button>
                    </Card>

                </div>
                <div className="flex flex-col items-center w-3/4">
                    <h2>Execs:</h2>
                    <div>
                        {exec}
                    </div>
                </div>
            </div>
        </div>

    )
}

const FuncDetailPage: React.FC = () => {
    const {funcId} = useParams()
    const [activeTab, setActiveTab] = useState<TabType>('Code')


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
        <main className="flex-1 w-full mx-auto text-lg h-full shadow-lg bg-gray-100">
            <div className="flex flex-col p-3 items-center justify-center bg-white">
                <div className="flex flex-row">
                    <Button onClick={() => setActiveTab('Dashboard')}>Dashboard</Button>
                    <Button onClick={() => setActiveTab('Code')}>Code</Button>
                    <Button onClick={() => setActiveTab('Runs')}> Runs</Button>
                    <Button onClick={() => setActiveTab('Settings')}>Settings</Button>
                </div>
            </div>

            {activeTab === 'Dashboard' && <h1> Dashboard</h1>}
            {activeTab === 'Code' && <EditorTab funcId={funcId} code={code} setCode={setCode}/>}
            {activeTab === 'Runs' && <RunTab funcId={funcId}/>}
            {activeTab === 'Settings' && <h1> Settings</h1>}
        </main>

    );
}


export default FuncDetailPage

