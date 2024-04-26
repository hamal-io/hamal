import React, {FC, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {Card, CardTitle} from "@/components/ui/card.tsx";
import {Switch} from "@/components/ui/switch.tsx";
import {Flow} from "@/types/flow.ts";
import {useSetFlowStatus} from "@/hook/flow.ts";

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
        setFlowStatus(flow.triggerId, status, abortController)
        return (() => abortController.abort())
    }

    useEffect(() => {
        if (statusResponse) {
            setActive(statusResponse.status === 'Active')
        }
    }, [statusResponse]);

    return (
        <Card onClick={handleClick} className={"flex flex-row items-center p-4"}>
            <CardTitle>
                <Switch checked={active} onCheckedChange={handleCheck} onClick={(e) => e.stopPropagation()}></Switch>
                <span className="pl-4">{flow.name} </span>
            </CardTitle>
        </Card>
    )
}