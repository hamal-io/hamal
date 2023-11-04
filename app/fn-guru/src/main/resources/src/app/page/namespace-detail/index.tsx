import React, {FC, ReactNode} from 'react'
import {useParams} from "react-router-dom";
import {ApiNamespace} from "../../../api/types";
import NamespaceSidebar from "./component/sidebar";
import {useApiGet} from "../../../hook";


interface NamespaceDetailPageProps {
    children: ReactNode;
}


const NamespaceDetailPage: FC<NamespaceDetailPageProps> = (props: NamespaceDetailPageProps) => {
    const {namespaceId} = useParams()

    const [namespace, isLoading, error] = useApiGet<ApiNamespace>(`v1/namespaces/${namespaceId}`)

    return (
        <main className="flex-1 w-full mx-auto text-lg h-full shadow-lg bg-gray-100">
            <div className="flex flex-row">
                <div className="flex flex-col items-start w-2/12">
                    <NamespaceSidebar/>
                </div>
                <div className="flex flex-col items-center w-10/12">
                    {JSON.stringify(isLoading)}
                    {JSON.stringify(namespace, null, 4)}
                    {props.children}
                </div>
            </div>
        </main>

    );
}


export default NamespaceDetailPage

