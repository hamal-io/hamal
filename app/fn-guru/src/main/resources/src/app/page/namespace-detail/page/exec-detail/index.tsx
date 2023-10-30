import React, {FC, useEffect, useState} from 'react'
import {getExec, listExecLogs} from "../../../../../api";
import {useParams} from "react-router-dom";
import {ApiExec, ApiExecLogList} from "../../../../../api/types";

const NamespaceExecDetailPage: React.FC = () => {
    const {execId} = useParams()

    const [loading, setLoading] = useState(false)
    const [exec, setExec] = useState<ApiExec>({} as ApiExec)
    const [execLogs, setExecLogs] = useState<ApiExecLogList>({logs: []})

    useEffect(() => {
        if (execId) {
            setLoading(true)
            listExecLogs(execId).then(response => {
                setExecLogs(response)
            })


            getExec(execId).then(response => {
                setExec((response))
                setLoading(false)
            })

        }
    }, [execId]);


    return (
        <main className="flex-1 w-full mx-auto text-lg h-full shadow-lg bg-gray-100">
            {JSON.stringify(exec, null, 4)}

            <h2> Logs: </h2>
            {execLogs.logs.map(log =>
                <div className={"flex flex-row"}>
                    <div>{log.id}:</div>
                    <div>{log.level}-</div>
                    <div>{log.message}</div>
                </div>
            )}
        </main>

    );
}


export default NamespaceExecDetailPage

