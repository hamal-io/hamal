import React, {FC, useEffect, useState} from 'react'
import {getExec} from "../../../api";
import {useParams} from "react-router-dom";
import {ApiExec} from "../../../api/types";

const ExecDetailPage: React.FC = () => {
    const {execId} = useParams()

    const [loading, setLoading] = useState(false)
    const [exec, setExec] = useState<ApiExec>({} as ApiExec)

    useEffect(() => {
        if (execId) {
            setLoading(true)
            getExec(execId).then(response => {
                setExec((response))
                setLoading(false)
            })
        }
    }, [execId]);


    return (
        <main className="flex-1 w-full mx-auto text-lg h-full shadow-lg bg-gray-100">
            {JSON.stringify(exec, null, 4)}
        </main>

    );
}


export default ExecDetailPage

