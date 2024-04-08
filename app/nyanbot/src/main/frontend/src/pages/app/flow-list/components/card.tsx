import React, {FC} from "react";
import {useNavigate} from "react-router-dom";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Switch} from "@/components/ui/switch.tsx";

type Props = {
    flow: Flow
}
export const FlowCard: FC<Props> = ({flow}) => {
    const navigate = useNavigate()

    const {id, name, status} = flow

    function handleClick(e: React.MouseEvent) {
        e.stopPropagation()
        e.preventDefault()
        navigate(`/flows/${id}`)
    }

    function handleCheck(e) {
        e.stopPropagation()
        e.preventDefault()
    }

    const active = status === "Active"

    return (
        <Card onClick={handleClick}>
            <CardHeader>
                <CardTitle>{name}</CardTitle>
            </CardHeader>
            <CardContent>
                <Switch checked={active} className={"float-right"} onCheckedChange={handleCheck}></Switch>
            </CardContent>
        </Card>
    )
}