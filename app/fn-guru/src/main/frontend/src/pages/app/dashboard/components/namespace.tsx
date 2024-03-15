import React, {cloneElement, FC, useEffect, useState} from "react";
import {Card, CardContent, CardDescription, CardHeader} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useNamespaceGet, useNamespaceUpdate} from "@/hook";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {FeatureObject} from "@/types";
import {Switch} from "@/components/ui/switch.tsx";
import {Button} from "@/components/ui/button.tsx";
import Element = React.JSX.Element;

const NamespaceDetailPage = () => {
    const [uiState] = useUiState()
    const [getNamespace, namespace, loading, error] = useNamespaceGet()
    const [updateNamespace, updateRequested, loading2, error2] = useNamespaceUpdate()
    const [features, setFeatures] = useState(new Map<string, boolean>([
        ["Schedule", false],
        ["Topic", false],
        ["Webhook", false],
        ["Endpoint", false]
    ]))

    useEffect(() => {
        const abortController = new AbortController()
        getNamespace(uiState.namespaceId, abortController)
        return (() => abortController.abort())
    }, [uiState]);

    useEffect(() => {
        if (namespace) {
            const x = namespace.features
            const t = new Map(features)
            const feats = namespace.features
            for (const [k, v] of Object.entries(feats)) {
                t.set(k, true)
            }
            setFeatures(t)
        }
    }, [namespace]);

    if (error) return "Error"
    if (loading) return "Loading..."

    function updateFeatures() {
        try {
            const answer: FeatureObject = {}
            features.forEach((isActive, ft) => {
                if (isActive) {
                    answer[ft] = -1
                }
            })
            updateNamespace(uiState.namespaceId, namespace.name, answer)

        } catch (e) {
            console.log(e)
        }
    }

    function toggle(feature: string) {
        const t = new Map(features)
        const state = features.get(feature)
        t.set(feature, !state)
        setFeatures(t)
    }

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Workloads"
                description="Select workflows for this namespace."
                actions={[
                    <Button variant={"default"} onClick={updateFeatures}>Apply</Button>
                ]}
            />
            <div className={"flex flex-col gap-4"}>
                <FeatureCard
                    label={"Schedules"}
                    description={"All kinds of timers"}
                    icon={<Timer/>}
                    checked={features.get("Schedule")}
                    onCheck={() => toggle("Schedule")}
                />
                <FeatureCard
                    label={"Topics"}
                    description={"Stay tuned"}
                    icon={<Layers3/>}
                    checked={features.get("Topic")}
                    onCheck={() => toggle("Topic")}

                />
                <FeatureCard
                    label={"Webhook"}
                    description={"Stay tuned"}
                    icon={<Webhook/>}
                    checked={features.get("Webhook")}
                    onCheck={() => toggle("Webhook")}

                />
                <FeatureCard
                    label={"Endpoint"}
                    description={"API yourself"}
                    icon={<Globe/>}
                    checked={features.get("Endpoint")}
                    onCheck={() => toggle("Endpoint")}
                />
            </div>
        </div>
    )
}

type FeatureProps = {
    label: string,
    description: string,
    icon: Element,
    onCheck: () => void
    checked: boolean
}

const FeatureCard: FC<FeatureProps> = ({label, description, onCheck, icon, checked}) => {
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

export default NamespaceDetailPage