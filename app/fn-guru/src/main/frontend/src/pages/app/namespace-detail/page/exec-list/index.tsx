import React from 'react'
import {ApiExecList} from "../../../../../api/types";
import {useNavigate, useParams} from "react-router-dom";
import {useApiGet} from "../../../../../hook";
import {Card} from "@/components/ui/card.tsx";


const NamespaceExecListPage: React.FC = () => {
    const {namespaceId} = useParams()
    const navigate = useNavigate()
    const [response, isLoading, error] = useApiGet<ApiExecList>(`v1/namespaces/${namespaceId}/execs`)

    console.log(response)
    console.log(isLoading)
    console.log(error)

    if (isLoading) return "Loading..."


    const content = response.execs.map(exec => (
        <Card
            key={exec.id}
            className="max-w-sm"
            onClick={() => navigate(`/namespaces/${namespaceId}/executions/${exec.id}`)}
        >
            <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                <p>{exec.id} : {exec.status}</p>
            </h5>
        </Card>
    ))

    return (
        <main className="flex-1 w-full mx-auto p-4 text-lg h-full  bg-gray-100">
            <div className="flex flex-col items-center justify-center">
                <p> Runs </p>
                {content}
            </div>
        </main>
    );
}

export default NamespaceExecListPage

