import React, {createContext, FC, ReactNode, useEffect} from 'react'
import {useParams} from "react-router-dom";
import Sidebar from "@/pages/app/flow-detail/components/sidebar";
import Index from "@/components/app/layout/authenticated";
import {useFlowGet} from "@/hook";
import {Flow} from "@/types";


type Props = {
    children: ReactNode;
}

export const flowContext = createContext<Flow | null>(null)

const FlowDetailPage: FC<Props> = ({children}) => {
    const {flowId} = useParams()

    const [getFlow, flow, loading, error] = useFlowGet()
    useEffect(() => {
        getFlow(flowId)
    }, [flowId]);

    return (
        <Index>
            <div className="relative flex flex-row min-h-screen ">
                <Sidebar/>
                <div className="p-4 border-l border-border w-full  ml-64">
                    <flowContext.Provider value={flow}>
                        {children}
                    </flowContext.Provider>
                </div>
            </div>
        </Index>
    );
}


export default FlowDetailPage

