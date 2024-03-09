import React, {cloneElement, FC, useEffect, useState} from "react";
import {Card, CardContent, CardDescription, CardHeader} from "@/components/ui/card.tsx";
import {Checkbox} from "@/components/ui/checkbox.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useNamespaceGet, useNamespaceUpdate} from "@/hook";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import Element = React.JSX.Element;
import {NamespaceFeature, NamespaceFeatures} from "@/types";
import update from "@/pages/app/workspace-detail/tab/namespace-list/components/update.tsx";

const NamespaceDetailPage = () => {
    const [uiState] = useUiState()
    const [getNamespace, namespace, loading, error] = useNamespaceGet()
    const [updateNamespace, updateRequested, loading, error] = useNamespaceUpdate()

    useEffect(() => {
        const abortController = new AbortController()
        getNamespace(uiState.namespaceId, abortController)
        return (() => abortController.abort())
    }, [uiState]);

    if (error) return "Error"
    if (loading) return "Loading..."

    function handleCheck(value: NamespaceFeature) {
        try {
            //TODO-204 updateNamespace(id,value)...
        } catch (e) {
            console.log(e)
        }
    }

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Workloads"
                description="Select workflows for this namespace."
                actions={[]}
            />
            <div className={"grid gap-4 grid-cols-2"}>
                <FeatureCard value={NamespaceFeatures.SCHEDULES}
                             label={"Schedules"}
                             description={"All kinds of timers"}
                             icon={<Timer/>}
                             onCheck={handleCheck}
                />
                <FeatureCard value={NamespaceFeatures.TOPICS}
                             label={"Topics"}
                             description={"Stay tuned"}
                             icon={<Layers3/>}
                             onCheck={handleCheck}

                />
                <FeatureCard value={NamespaceFeatures.WEBHOOKS}
                             label={"Webhooks"}
                             description={"Stay tuned"}
                             icon={<Webhook/>}
                             onCheck={handleCheck}

                />
                <FeatureCard value={NamespaceFeatures.ENDPOINTS}
                             label={"Endpoints"}
                             description={"API yourself"}
                             icon={<Globe/>}
                             onCheck={handleCheck}
                />
            </div>
        </div>
    )
}

type FeatureProps = {
    value: number
    label: string,
    description: string,
    icon: Element,
    checked: boolean,
    onCheck: (NamespaceFeatures) => void
}

const FeatureCard: FC<FeatureProps> = ({value, label, description, onCheck, icon, checked}) => {
    const _icon = cloneElement(icon, {
        size: 32
    })

    return (
        <Card>
            <CardHeader className={"flex flex-row justify-between"}>
                {_icon}
                {label}
                <Checkbox checked={checked} onCheckedChange={() => onCheck(value)}></Checkbox>
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