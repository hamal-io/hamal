import React, {FC, useEffect, useState} from "react";
import {useNamespaceUpdate} from "@/hook";
import {Namespace} from "@/types";
import {FeatureCard} from "@/pages/app/dashboard/components/feature/card.tsx";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";


type Props = { namespace: Namespace }
const FeatureTab: FC<Props> = ({namespace}) => {
    const [updateNamespace, updateResponse, loading, error] = useNamespaceUpdate()
    const [activeFeatures, setActiveFeatures] = useState(new Map([
            ["schedule", false],
            ["topic", false],
            ["webhook", false],
            ["endpoint", false]
        ]
    ))

    useEffect(() => {
        const updateMap = {...activeFeatures}
        for (const [k, v] of Object.keys(namespace.features)) {
            updateMap[k] = true
        }
        setActiveFeatures(updateMap)
    }, [namespace]);

    function update(updateMap: Map<string, boolean>) {
        try {
            const abortController = new AbortController()
            //updateNamespace(namespace.id, namespace.name, , abortController)
            return (() => abortController.abort())
        } catch (e) {
            console.log(e)
        }
    }

    function toggle(key: string) {
        const updateMap = {...activeFeatures}
        updateMap[key] = !activeFeatures[key]
        setActiveFeatures(updateMap)
        update(updateMap)
    }

    if (error) return "Error"
    const [schedule, topic, webhook, endpoint] = activeFeatures.values()

    return (
        <div className="pt-8 px-8">
            <div className={"flex flex-col gap-4"}>
                <FeatureCard
                    label={"Schedule"}
                    description={"All kinds of timers"}
                    icon={<Timer/>}
                    checked={schedule}
                    onCheck={() => toggle("schedule")}
                />
                <FeatureCard
                    label={"Topic"}
                    description={"Stay tuned"}
                    icon={<Layers3/>}
                    checked={topic}
                    onCheck={() => toggle("topic")}

                />
                <FeatureCard
                    label={"Webhook"}
                    description={"Stay tuned"}
                    icon={<Webhook/>}
                    checked={webhook}
                    onCheck={() => toggle("webhook")}

                />
                <FeatureCard
                    label={"Endpoint"}
                    description={"API yourself"}
                    icon={<Globe/>}
                    checked={endpoint}
                    onCheck={() => toggle("endpoint")}
                />
            </div>
        </div>
    )
}
export default FeatureTab


