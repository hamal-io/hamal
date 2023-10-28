import React, {FC, useEffect, useState} from 'react'
import {useParams} from "react-router-dom";
import {ApiNamespace} from "../../../api/types";
import {getNamespace} from "../../../api/namespace.ts";

const NamespaceDetailPage: FC = () => {
    const {namespaceId} = useParams()

    const [loading, setLoading] = useState(false)
    const [namespace, setNamespace] = useState<ApiNamespace>({} as ApiNamespace)

    useEffect(() => {
        if (namespaceId) {
            setLoading(true)
            getNamespace(namespaceId).then(response => {
                setNamespace((response))
                setLoading(false)
            })

        }
    }, [namespaceId]);


    return (
        <main className="flex-1 w-full mx-auto text-lg h-full shadow-lg bg-gray-100">
            {JSON.stringify(namespace, null, 4)}
        </main>

    );
}


export default NamespaceDetailPage

