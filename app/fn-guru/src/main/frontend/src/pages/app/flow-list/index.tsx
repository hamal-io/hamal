import React, {useEffect, useState} from 'react'
import {useNavigate} from "react-router-dom";
import {ApiFlow, ApiFlowList} from "@/api/types";
import {useApiGet, useApiPost,} from "@/hook";
import {useAuth} from "@/hook/auth.ts";
import {Separator} from "@/components/ui/separator.tsx";
import List from "@/pages/app/flow-list/components/list.tsx";
import {Card} from "@/components/ui/card.tsx";

const FlowListPage: React.FC = () => {

    const navigate = useNavigate()
    const [auth] = useAuth()

    const [data, isLoading, error] = useApiGet<ApiFlowList>(`v1/groups/${auth.groupId}/flows`)

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    // const list = data.flows.filter(flow => flow.name !== "__default__").map(flow => (
    //     <Card
    //         key={flow.id}
    //         className="w-full my-1 text-gray-900 hover:bg-gray-900  hover:text-gray-50 shadow-gray-200"
    //         onClick={() => navigate(`/flows/${flow.id}`)}
    //     >
    //         <h2 className="text-lg tracking-tight">
    //             <p>{flow.name}</p>
    //         </h2>
    //     </Card>
    // ))

    return (
        // <main className="flex-1 w-full pt-2 mx-auto text-lg  shadow-lg bg-gray-200">
        //     <section className="container p-4 mx-auto max-w-3xl">
        //         <div className="sm:flex sm:items-center sm:justify-between ">
        //             <div>
        //                 <div className="flex items-center gap-x-3">
        //                     <h2 className="text-lg font-medium text-gray-800 dark:text-white">Flows</h2>
        //                 </div>
        //                 <p className="mt-1 text-sm text-gray-500 dark:text-gray-300">Organise your workflows</p>
        //             </div>
        //
        //             <div className="flex items-center mt-4 gap-x-3">
        //                 <CreateFlowModalButton groupId={auth.groupId}/>
        //             </div>
        //         </div>
        //     </section>
        //
        //     <section className="container mx-auto max-w-3xl">
        //         <div className="flex flex-col py-6 items-center justify-center">
        //             {list}
        //         </div>
        //     </section>
        // </main>

        <div>
            <List groupId={auth.groupId}/>
        </div>

    )

}


export default FlowListPage;
