import React, {cloneElement, Component, FC, ReactElement, ReactNode} from "react";
import {Card, CardContent, CardDescription, CardHeader} from "@/components/ui/card.tsx";
import {Checkbox} from "@/components/ui/checkbox.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useNamespaceUpdate} from "@/hook";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {IconProps} from "@radix-ui/react-icons/dist/types";
import {Button} from "@/components/ui/button.tsx";
import Element = React.JSX.Element;

const NamespaceDetailPage = () => {
    const [uiState] = useUiState()

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Workloads"
                description="Select workflows for this namespace."
                actions={[]}
            />
            <div className={"grid gap-4 grid-cols-2"}>
                <FeatureCard value={0} label={"Schedules"} description={"All kinds of timers"} icon={<Timer/>}/>
                <FeatureCard value={1} label={"Topics"} description={"Stay tuned"} icon={<Layers3/>}/>
                <FeatureCard value={2} label={"Webhooks"} description={"Stay tuned"} icon={<Webhook/>}/>
                <FeatureCard value={3} label={"Endpoints"} description={"API yourself"} icon={<Globe/>}/>
            </div>
        </div>
    )
}

type FeatureProps = {
    value: number
    label: string,
    description: string,
    icon: Element
}
const FeatureCard: FC<FeatureProps> = ({value, label, description, icon}) => {
    const [updateNamespace, updateRequested, loading, error] = useNamespaceUpdate()

    const _icon = cloneElement(icon, {
        size: 32
    })

    return (
        <Card>
            <CardHeader className={"flex flex-row justify-between"}>
                {_icon}
                {label}
                <Checkbox></Checkbox>
            </CardHeader>
            <CardContent>
                <CardDescription>
                    {description}
                </CardDescription>
            </CardContent>
        </Card>
    )
}

export default NamespaceDetailPage