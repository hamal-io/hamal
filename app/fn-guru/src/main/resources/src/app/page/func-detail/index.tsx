import React, {FC, useEffect, useState} from 'react'
import {useNavigate, useParams} from "react-router-dom";
import Editor from "../../../component/editor";
import {Button, Card} from "flowbite-react";
import {ApiExecSimple, ApiFunc} from "../../../api/types";
import {getFunction, invokeAdhoc, invokeFunc, listExecs, updateFunc} from "../../../api";

type TabType = 'Dashboard' | 'Editor' | 'Runs' | 'Settings'


interface EditorTabProps {
    code: string;
    funcId: string;
    setCode: (code: string) => void;
}

const EditorTab: FC<EditorTabProps> = (props) => {
    return (
        <div className="flex flex-col items-center justify-center">
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
                }}> Save </Button>

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
            className="max-w-sm"
        >
            <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                <p>{exec.id} : {exec.status}</p>
            </h5>
        </Card>
    ))


    return (
        <div className="flex flex-col items-center justify-center">
            <h2> Run </h2>

            <h2> Triggers: </h2>
            <Card>
                <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                    <p> Manual Trigger </p>
                    <Button onClick={() => {
                        invokeFunc(props.funcId, {}).then(response => {
                            console.log(response)

                            setLoading(true)
                            listExecs({funcId: props.funcId, limit: 10}).then(response => {
                                setExecs(response.execs)
                                setLoading(false)
                            })
                        })
                    }}> Invoke </Button>
                </h5>
            </Card>

            <h2>Execs:</h2>
            <div>
                {exec}
            </div>
        </div>
    )
}

const FuncDetailPage: React.FC = () => {
    const {funcId} = useParams()
    const [activeTab, setActiveTab] = useState<TabType>('Dashboard')


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
            <div className="flex p-3 items-center justify-center bg-gray-300">
                <p> Function {funcId} </p>
            </div>

            <aside
                id="default-sidebar"
                className="m-4 fixed w-64 h-screen transition-transform -translate-x-full sm:translate-x-0"
                aria-label="Sidebar"
            >
                <div className="h-screen px-3 py-4 overflow-y-auto bg-gray-200">
                    <ul className="space-y-2 font-medium">
                        <li>
                            <a
                                onClick={() => setActiveTab('Dashboard')}
                                className="flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
                                <svg className="w-5 h-5 text-gray-500 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white"
                                     aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 22 21">
                                    <path d="M16.975 11H10V4.025a1 1 0 0 0-1.066-.998 8.5 8.5 0 1 0 9.039 9.039.999.999 0 0 0-1-1.066h.002Z"/>
                                    <path d="M12.5 0c-.157 0-.311.01-.565.027A1 1 0 0 0 11 1.02V10h8.975a1 1 0 0 0 1-.935c.013-.188.028-.374.028-.565A8.51 8.51 0 0 0 12.5 0Z"/>
                                </svg>
                                <span className="ml-3">Dashboard</span>
                            </a>
                        </li>
                        <li>
                            <a
                                onClick={() => setActiveTab('Editor')}
                                className="flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
                                <svg
                                    className="flex-shrink-0 w-5 h-5 text-gray-500 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white"
                                    aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 18 18">
                                    <path
                                        d="M6.143 0H1.857A1.857 1.857 0 0 0 0 1.857v4.286C0 7.169.831 8 1.857 8h4.286A1.857 1.857 0 0 0 8 6.143V1.857A1.857 1.857 0 0 0 6.143 0Zm10 0h-4.286A1.857 1.857 0 0 0 10 1.857v4.286C10 7.169 10.831 8 11.857 8h4.286A1.857 1.857 0 0 0 18 6.143V1.857A1.857 1.857 0 0 0 16.143 0Zm-10 10H1.857A1.857 1.857 0 0 0 0 11.857v4.286C0 17.169.831 18 1.857 18h4.286A1.857 1.857 0 0 0 8 16.143v-4.286A1.857 1.857 0 0 0 6.143 10Zm10 0h-4.286A1.857 1.857 0 0 0 10 11.857v4.286c0 1.026.831 1.857 1.857 1.857h4.286A1.857 1.857 0 0 0 18 16.143v-4.286A1.857 1.857 0 0 0 16.143 10Z"/>
                                </svg>
                                <span className="flex-1 ml-3 whitespace-nowrap">Editor</span>
                            </a>
                        </li>
                        <li>
                            <a
                                onClick={() => setActiveTab('Runs')}
                                className="flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
                                <svg
                                    className="flex-shrink-0 w-5 h-5 text-gray-500 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white"
                                    aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 18">
                                    <path
                                        d="M14 2a3.963 3.963 0 0 0-1.4.267 6.439 6.439 0 0 1-1.331 6.638A4 4 0 1 0 14 2Zm1 9h-1.264A6.957 6.957 0 0 1 15 15v2a2.97 2.97 0 0 1-.184 1H19a1 1 0 0 0 1-1v-1a5.006 5.006 0 0 0-5-5ZM6.5 9a4.5 4.5 0 1 0 0-9 4.5 4.5 0 0 0 0 9ZM8 10H5a5.006 5.006 0 0 0-5 5v2a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-2a5.006 5.006 0 0 0-5-5Z"/>
                                </svg>
                                <span className="flex-1 ml-3 whitespace-nowrap">Runs</span>
                            </a>
                        </li>
                        <li>
                            <a
                                onClick={() => setActiveTab('Settings')}
                                className="flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
                                <svg
                                    className="flex-shrink-0 w-5 h-5 text-gray-500 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white"
                                    aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 18 20">
                                    <path
                                        d="M17 5.923A1 1 0 0 0 16 5h-3V4a4 4 0 1 0-8 0v1H2a1 1 0 0 0-1 .923L.086 17.846A2 2 0 0 0 2.08 20h13.84a2 2 0 0 0 1.994-2.153L17 5.923ZM7 9a1 1 0 0 1-2 0V7h2v2Zm0-5a2 2 0 1 1 4 0v1H7V4Zm6 5a1 1 0 1 1-2 0V7h2v2Z"/>
                                </svg>
                                <span className="flex-1 ml-3 whitespace-nowrap">Settings</span>
                            </a>
                        </li>
                    </ul>
                </div>
            </aside>

            <div className="p-4 sm:ml-64">
                {activeTab === 'Dashboard' && <h1> Dashboard</h1>}
                {activeTab === 'Editor' && <EditorTab funcId={funcId} code={code} setCode={setCode}/>}
                {activeTab === 'Runs' && <RunTab funcId={funcId}/>}
                {activeTab === 'Settings' && <h1> Settings</h1>}
            </div>
        </main>

    );
}


export default FuncDetailPage

