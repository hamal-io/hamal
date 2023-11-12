import React, {createContext, FC, ReactNode, useEffect, useState} from 'react'
import {useParams} from "react-router-dom";
import {ApiFlow, ApiFuncList, ApiFuncSimple} from "@/api/types";
import Sidebar from "@/pages/app/flow-detail/components/sidebar";
import {useApiGet, useApiGetAction} from "@/hook";
import Authenticated from "@/components/app/authenticated.tsx";
import {useApiFuncList} from "@/hook/api/func.ts";


type Props = {
    children: ReactNode;
}

export const FlowContext = createContext<ApiFlow | null>(null)

const useFlowDetail = () => {
    const {flowId} = useParams()
    const [loading, setLoading] = useState(true)

    const [flow, isLoading, error] = useApiGet<ApiFlow>(`v1/flows/${flowId}`)
    const [getFuncs, funcs] = useApiGetAction<ApiFuncList>()

    useEffect(() => {
        if (flow !== null) {
            getFuncs(`v1/flows/${flowId}/funcs`)
        }
    }, [flow]);

    return [loading, flow, funcs,]
}

const FlowDetailPage: FC<Props> = ({children}) => {
    const [loading, flow, funcs] = useFlowDetail()

    return (
        <Authenticated>
            <div className="relative flex flex-row min-h-screen ">
                <Sidebar/>
                <div className="p-4 border-l border-border w-full  ml-64">
                    <FlowContext.Provider value={flow}>
                        {children}
                    </FlowContext.Provider>
                </div>
            </div>
        </Authenticated>
    );
}


export default FlowDetailPage

