import React, {cloneElement, FC} from "react";
import {Card, CardContent, CardDescription, CardHeader} from "@/components/ui/card.tsx";
import {Switch} from "@/components/ui/switch.tsx";
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
        <Card>
            <CardHeader className={"flex flex-row justify-between"}>
                {_icon}
                {label}
                <Switch checked={checked} onCheckedChange={() => onCheck()}></Switch>
            </CardHeader>
            <CardContent>
                <CardDescription>
                    {description}
                </CardDescription>
            </CardContent>
        </Card>
    )
}