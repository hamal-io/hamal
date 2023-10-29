import React, {useEffect, useState} from 'react'
import {ApiExecSimple} from "../../../../api/types";
import {listExecs} from "../../../../api";
import {Card} from "flowbite-react";
import {useNavigate} from "react-router-dom";


const RunListPage: React.FC = () => {
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false);
    const [execs, setExecs] = useState<ApiExecSimple[]>([])

    useEffect(() => {
        setLoading(true)
        listExecs({groupId: '1', limit: 10}).then(response => {
            setExecs(response.execs)
            setLoading(false)
        })
    }, []);

    const content = execs.map(exec => (
        <Card
            key={exec.id}
            className="max-w-sm"
            onClick={() => navigate(`/execs/${exec.id}`)}
        >
            <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                <p>{exec.id} : {exec.status}</p>
            </h5>
        </Card>
    ))

    return (
        <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
            <div className="flex flex-col items-center justify-center">
                <p> Runs </p>
                {content}
            </div>
        </main>
    );
}

export default RunListPage

