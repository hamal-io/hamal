import React, {FC, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {Card, CardTitle} from "@/components/ui/card.tsx";
import {Switch} from "@/components/ui/switch.tsx";
import {useSetFlowStatus} from "@/hooks/flow.ts";
import {Flow} from "@/types/flow.ts";


type Props = {
    flow: Flow
}
export const FlowCard: FC<Props> = ({flow}) => {
    const navigate = useNavigate()
    const [active, setActive] = useState<boolean>(flow.status === "Active")
    const [setFlowStatus, statusResponse] = useSetFlowStatus()

    function handleClick() {
        navigate(`/flows/${flow.id}`)
    }

    function handleCheck() {
        const status = active === false ? 'activate' : 'deactivate'
        const abortController = new AbortController()
        setFlowStatus(flow.id, status, abortController)
    }

    useEffect(() => {
        if (statusResponse) {
            setActive(statusResponse.status === 'Active')
        }
    }, [statusResponse]);

    return (
        <Card onClick={handleClick} className={"flex flex-row items-center justify-between p-4"}>
            <CardTitle>{flow.name}</CardTitle>
            <Switch checked={active} onCheckedChange={handleCheck} onClick={(e) => e.stopPropagation()}></Switch>
        </Card>
    )
}