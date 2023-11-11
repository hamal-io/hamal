import React, {createContext, FC, ReactNode, useEffect} from 'react'
import {useParams} from "react-router-dom";
import {ApiFlow} from "@/api/types";
import Sidebar from "@/pages/app/flow-detail/components/sidebar";
import {useApiGet} from "@/hook";
import Authenticated from "@/components/app/authenticated.tsx";


type Props = {
    children: ReactNode;
}

export const FlowContext = createContext<ApiFlow | null>(null)

const FlowDetailPage: FC<Props> = ({children}) => {
    const {flowId} = useParams()
    const [flow, isLoading, error] = useApiGet<ApiFlow>(`v1/flows/${flowId}`)


    return (
        <Authenticated>
            <div className="relative flex flex-row min-h-screen bg-gray-100">
                <Sidebar/>
                <div className="p-4 border-l bg-background border-border w-full  ml-64">
                    <FlowContext.Provider value={flow}>
                        {children}
                    </FlowContext.Provider>
                </div>
            </div>
        </Authenticated>
    );
}


export default FlowDetailPage

