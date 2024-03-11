import React, {cloneElement, FC, useEffect, useState} from "react";
import {Card, CardContent, CardDescription, CardHeader} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {useNamespaceGet, useNamespaceUpdate} from "@/hook";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {Feature, featuresMap} from "@/types";
import {Switch} from "@/components/ui/switch.tsx";
import Element = React.JSX.Element;


const NamespaceDetailPage = () => {
    const [uiState] = useUiState()
    const [getNamespace, namespace, loading, error] = useNamespaceGet()
    const [updateNamespace, updateRequested, loading2, error2] = useNamespaceUpdate()
    const [features, setFeatures] = useState(featuresMap)

    useEffect(() => {
        const abortController = new AbortController()
        getNamespace(uiState.namespaceId, abortController)
        return (() => abortController.abort())
    }, [uiState]);

    useEffect(() => {
        if (namespace) {
            const t = new Map(featuresMap)
            /*
            const acc = namespace.features.split(',')
            acc.forEach(f => {
                switch (f) {
                    case 'schedules':
                        t.set(Feature.SCHEDULES, true)
                        break
                    case 'topics':
                        t.set(Feature.TOPICS, true)
                        break
                    case 'webhooks':
                        t.set(Feature.WEBHOOKS, true)
                        break
                    case 'endpoints':
                        t.set(Feature.ENDPOINTS, true)
                        break
                    default:
                        break
                }
            })
            */
             setFeatures(t)
        }
    }, [namespace]);

    if (error) return "Error"
    if (loading) return "Loading..."

    function handleCheck(value: Feature) {
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
            <div className={"flex flex-col gap-4"}>
                <FeatureCard value={Feature.SCHEDULES}
                             label={"Schedules"}
                             description={"All kinds of timers"}
                             icon={<Timer/>}
                             checked={features.get(Feature.SCHEDULES)}
                             onCheck={handleCheck}
                />
                <FeatureCard value={Feature.TOPICS}
                             label={"Topics"}
                             description={"Stay tuned"}
                             icon={<Layers3/>}
                             checked={features.get(Feature.TOPICS)}
                             onCheck={handleCheck}

                />
                <FeatureCard value={Feature.WEBHOOKS}
                             label={"Webhooks"}
                             description={"Stay tuned"}
                             icon={<Webhook/>}
                             checked={features.get(Feature.WEBHOOKS)}
                             onCheck={handleCheck}

                />
                <FeatureCard value={Feature.ENDPOINTS}
                             label={"Endpoints"}
                             description={"API yourself"}
                             icon={<Globe/>}
                             checked={features.get(Feature.ENDPOINTS)}
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
    onCheck: (f: Feature) => void
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
                <Switch checked={checked} onCheckedChange={() => onCheck(value)}></Switch>
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