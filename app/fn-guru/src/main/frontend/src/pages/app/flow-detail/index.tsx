import React, {createContext, FC, ReactNode, useEffect} from 'react'
import {useParams} from "react-router-dom";
import Sidebar from "@/pages/app/namespace-detail/components/sidebar";
import Authenticated from "@/components/app/authenticated.tsx";
import {useNamespaceGet} from "@/hook";
import {Namespace} from "@/types";


type Props = {
    children: ReactNode;
}

export const NamespaceContext = createContext<Namespace | null>(null)

const NamespaceDetailPage: FC<Props> = ({children}) => {
    const {namespaceId} = useParams()

    const [getNamespace, namespace, loading, error] = useNamespaceGet()
    useEffect(() => {
        getNamespace(namespaceId)
    }, [namespaceId]);

    return (
        <Authenticated>
            <div className="relative flex flex-row min-h-screen ">
                <Sidebar/>
                <div className="p-4 border-l border-border w-full  ml-64">
                    <NamespaceContext.Provider value={namespace}>
                        {children}
                    </NamespaceContext.Provider>
                </div>
            </div>
        </Authenticated>
    );
}


export default NamespaceDetailPage

