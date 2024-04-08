import React, {useState} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {Button} from "@/components/ui/button.tsx";
import {FlowCard} from "@/pages/app/flow-list/components/card.tsx";
import Create from "@/pages/app/flow-list/components/create.tsx";


const initialFlows: Flow[] = [{id: "1", status: "Active", name: "SampleFlow"}]

const FlowListPage = () => {
    const [flowList, setFlowList] = useState<Flow[]>(initialFlows)

    function addFlow(req: FlowCreateRequested) {
        setFlowList(prevFlowList => [...prevFlowList, {
            id: req.id,
            name: req.name,
            status: req.status
        }])
    }

    const createButton = <Button>Create</Button>

    return (
        <>
            <div className="pt-2 px-2">
                <PageHeader title={"Flows"} description={""} actions={[
                    <Create trigger={createButton} onCreate={addFlow}/>
                ]}/>
                <ol className="flex flex-col gap-4 cursor-pointer">
                    {flowList.map(flow =>
                        <li key={flow.id}>
                            <FlowCard flow={flow}/>
                        </li>
                    )}
                </ol>
            </div>

        </>
    )
}

export default FlowListPage;