import React, {FC, ReactNode} from 'react'
import {useParams} from "react-router-dom";
import {ApiFlow} from "@/api/types";
import Flowsidebar from "./component/sidebar";
import {useApiGet} from "@/hook";


interface FlowDetailPageProps {
    children: ReactNode;
}


const FlowDetailPage: FC<FlowDetailPageProps> = (props: FlowDetailPageProps) => {
    const {flowId} = useParams()
    const [flow, isLoading, error] = useApiGet<ApiFlow>(`v1/flows/${flowId}`)

    return (
        <main className="flex-1 w-full mx-auto text-lg h-full shadow-lg bg-gray-200">
            <div className="flex flex-row">
                <div className="flex flex-col items-start w-2/12">
                    <Flowsidebar/>
                </div>
                <div className="flex flex-col items-center w-10/12">
                    {props.children}
                </div>
            </div>
        </main>

    );
}


export default FlowDetailPage

