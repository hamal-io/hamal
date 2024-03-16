import React, {cloneElement, FC} from "react";
import {Card, CardDescription, CardTitle} from "@/components/ui/card.tsx";
import {Switch} from "@/components/ui/switch.tsx";
import {Avatar} from "@/components/ui/avatar.tsx";
import Element = JSX.Element;


type Props = {
    label: string,
    description: string,
    icon: Element,
    onCheck: () => void
    checked: boolean
}
export const FeatureCard: FC<Props> = ({label, description, onCheck, icon, checked}) => {
    const _icon = cloneElement(icon, {
        size: 32
    })

    return (
        <Card className={"flex flex-row items-center justify-start w-3/5 p-4"}>
            <Avatar className={"w-1/4"}>
                {_icon}
            </Avatar>
            <div className={"flex flex-col w-1/3"}>
                <CardTitle>{label}</CardTitle>
                <CardDescription>{description}</CardDescription>
            </div>
            <div>
                <Switch checked={checked} onCheckedChange={() => onCheck()}></Switch>
            </div>
        </Card>
    )
}