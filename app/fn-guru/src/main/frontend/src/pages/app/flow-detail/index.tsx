import React, {createContext, FC, ReactNode, useEffect} from 'react'
import {useParams} from "react-router-dom";
import Sidebar from "@/pages/app/flow-detail/components/sidebar";
import Authenticated from "@/components/app/authenticated.tsx";
import {useFlowGet} from "@/hook";
import {flow} from "@/types";


type Props = {
    children: ReactNode;
}

export const flowContext = createContext<flow | null>(null)

const flowDetailPage: FC<Props> = ({children}) => {
    const {flowId} = useParams()

    const [getflow, flow, loading, error] = useFlowGet()
    useEffect(() => {
        getflow(flowId)
    }, [flowId]);

    return (
        <Authenticated>
            <div className="relative flex flex-row min-h-screen ">
                <Sidebar/>
                <div className="p-4 border-l border-border w-full  ml-64">
                    <flowContext.Provider value={flow}>
                        {children}
                    </flowContext.Provider>
                </div>
            </div>
        </Authenticated>
    );
}


export default flowDetailPage

