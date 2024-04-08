import React, {useEffect, useState} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {Button} from "@/components/ui/button.tsx";
import {FlowCard} from "@/pages/app/flow-list/components/card.tsx";
import Create from "@/pages/app/flow-list/components/create.tsx";
import {useFlowList} from "@/hooks/flow.ts";


const FlowListPage = () => {
    //const [flowList, setFlowList] = useState<Flow[]>([])
    const [listFlows, flowList, loading, error] = useFlowList()
    /*function addFlow(req: Flow) {
        setFlowList(prevFlowList => [...prevFlowList, {
            id: req.id,
            name: req.name,
            status: req.status
        }])
    }*/

    useEffect(() => {
        const abortController = new AbortController();
        listFlows(abortController)
        return (() => abortController.abort())
    }, []);

    const createButton = <Button>Create</Button>

    if (!flowList) return "Loading..."
    if (error) return "Error"


    return (
        <>
            <div className="pt-2 px-2">
                <PageHeader title={"Flows"} description={""} actions={[
                    <Create trigger={createButton} /*onCreate={addFlow}*//>
                ]}/>
                {flowList.flows.length !== 0 ?
                    <ol className="flex flex-col gap-4 cursor-pointer">
                        {flowList.flows.map(flow =>
                            <li key={flow.id}>
                                <FlowCard flow={flow}/>
                            </li>
                        )}
                    </ol> : null

                }


            </div>

        </>
    )
}

export default FlowListPage;