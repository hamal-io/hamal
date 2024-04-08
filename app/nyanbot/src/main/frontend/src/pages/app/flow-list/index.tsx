import React, {FC, useState} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {useNavigate} from "react-router-dom";

const FlowListPage = () => {
    const [flowList, setFlowList] = useState<Flow[]>([
        {name: "SampleFlow"}
    ])

    function handleCreate() {
        setFlowList(prevState => [...prevState, {name: "New Flow"}])
    }

    return (
        <div className="pt-2 px-2">
            <PageHeader title={"Flows"} description={""} actions={[
                <Button onClick={handleCreate}>
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

type FlowProps = {
    name: string;
    description: string;
}
export const FlowCard: FC<FlowProps> = ({name, description}) => {
    const navigate = useNavigate()

    function handleClick() {
        //navigate("/")
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>{name}</CardTitle>
            </CardHeader>
            <CardContent>
                <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                    <div className="flex justify-between py-3 gap-x-4">
                        {description}
                    </div>
                </dl>
                <Button onClick={handleClick}>
                    Options
                </Button>
            </CardContent>

        </Card>
    )
}

