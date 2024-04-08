import React, {FC, useState} from "react";
import {useNavigate} from "react-router-dom";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Switch} from "@/components/ui/switch.tsx";


type Props = {
    flow: Flow
}
export const FlowCard: FC<Props> = ({flow}) => {
    const navigate = useNavigate()
    const [active, setActive] = useState<boolean>(false)

    const {id, name, status} = flow


    function handleClick() {
        navigate(`/flows/${id}`)
    }

    function handleCheck() {
        setActive(!active)
    }


    return (
        <Card onClick={handleClick} className={"flex flex-row items-center justify-between p-4"}>
            <CardTitle className={""}>{name}</CardTitle>
            <Switch checked={active} onCheckedChange={handleCheck} onClick={(e) => e.stopPropagation()}></Switch>
        </Card>
    )
}