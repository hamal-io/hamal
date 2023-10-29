import React, {FC, ReactNode, useEffect, useState} from 'react'
import {useParams} from "react-router-dom";
import {ApiNamespace} from "../../../../api/types";
import {getNamespace} from "../../../../api/namespace.ts";
import NamespaceSidebar from "./component/sidebar";


interface NamespaceDetailPageProps {
    children: ReactNode;
}


const NamespaceDetailPage: FC<NamespaceDetailPageProps> = (props: NamespaceDetailPageProps) => {
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
            <div className="flex flex-row">
                <div className="flex flex-col items-start w-2/12">
                    <NamespaceSidebar/>
                </div>
                <div className="flex flex-col items-center w-10/12">
                    {JSON.stringify(namespace, null, 4)}
                    {props.children}
                </div>
            </div>
        </main>

    );
}


export default NamespaceDetailPage

