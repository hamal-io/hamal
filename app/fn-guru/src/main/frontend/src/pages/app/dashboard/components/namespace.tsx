import React, {cloneElement, FC, useEffect, useState} from "react";
import {Card, CardContent, CardDescription, CardHeader} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useNamespaceGet, useNamespaceUpdate} from "@/hook";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {Feature, FeatureObject} from "@/types";
import {Switch} from "@/components/ui/switch.tsx";
import Element = React.JSX.Element;


const featureObjects: Feature = {
    schedule: {"Schedule": 0},
    topic: {"Topic": 1},
    webhook: {"Webhook": 2},
    endpoint: {"Endpoint": 3},
}


const {schedule, topic, webhook, endpoint} = featureObjects

const NamespaceDetailPage = () => {
    const [uiState] = useUiState()
    const [getNamespace, namespace, loading, error] = useNamespaceGet()
    const [updateNamespace, updateRequested, loading2, error2] = useNamespaceUpdate()
    const [features, setFeatures] = useState(new Map<FeatureObject, boolean>([
        [schedule, false],
        [topic, false],
        [webhook, false],
        [endpoint, false]
    ]))

    useEffect(() => {
        const abortController = new AbortController()
        getNamespace(uiState.namespaceId, abortController)
        return (() => abortController.abort())
    }, [uiState]);

    useEffect(() => {
        if (namespace) {
            const t = new Map(features)
            const feats = namespace.features
            for (const [k, v] of Object.entries(feats)) {
                const f: FeatureObject = {}
                f[k] = v
                t.set(f, true)
            }
            setFeatures(t)
        }
    }, [namespace]);

    if (error) return "Error"
    if (loading) return "Loading..."

    function updateFeatures() {
        try {
            const presentFeatures: FeatureObject = features.keys()
            updateNamespace(uiState.namespaceId,namespace.name, )
        } catch (e) {
            console.log(e)
        }
    }

    function toggle(feature: FeatureObject) {
        const t = new Map(features)
        const state = features.get(feature)
        t.set(feature, !state)
        setFeatures(t)

        updateFeatures()
    }

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Workloads"
                description="Select workflows for this namespace."
                actions={[]}
            />
            <div className={"flex flex-col gap-4"}>
                <FeatureCard label={"Schedules"}
                             description={"All kinds of timers"}
                             icon={<Timer/>}
                             checked={features.get(schedule)}
                             onCheck={() => toggle(schedule)}
                />
                <FeatureCard
                    label={"Topics"}
                    description={"Stay tuned"}
                    icon={<Layers3/>}
                    checked={features.get(topic)}
                    onCheck={() => toggle(topic)}

                />
                <FeatureCard
                    label={"Webhook"}
                    description={"Stay tuned"}
                    icon={<Webhook/>}
                    checked={features.get(webhook)}
                    onCheck={() => toggle(webhook)}

                />
                <FeatureCard
                    label={"Endpoint"}
                    description={"API yourself"}
                    icon={<Globe/>}
                    checked={features.get(endpoint)}
                    onCheck={() => toggle(endpoint)}
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