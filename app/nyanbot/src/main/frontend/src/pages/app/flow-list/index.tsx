import React, {useRef, useState} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {Button} from "@/components/ui/button.tsx";
import {FlowCard} from "@/pages/app/flow-list/components/card.tsx";


const initialFlows: Flow[] = [{name: "SampleFlow"}]

const FlowListPage = () => {
    const [flowList, setFlowList] = useState<Flow[]>(initialFlows)
    const flowIdx = useRef<number>(0);

    function handleCreate() {
        flowIdx.current += 1
        setFlowList(prevState => [...prevState, {name: `New Flow ${flowIdx.current}`}])
    }

    return (
        <div className="pt-2 px-2">
            <PageHeader title={"Flows"} description={""} actions={[
                <Button size={"lg"} onClick={handleCreate}>
                    + Create Flow
                </Button>
            ]}/>
            <ol className="flex flex-col gap-4 cursor-pointer">
                {flowList.map(flow =>
                    <li key={flow.name}>
                        <FlowCard name={flow.name} description={""}/>
                    </li>
                )}
            </ol>
        </div>
    )
}

export default FlowListPage;



