import React, {FC, useEffect, useState} from "react";
import {useNamespaceUpdate} from "@/hook";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {FeatureHotObject, Namespace} from "@/types";
import {FeatureCard} from "@/pages/app/dashboard/components/feature-components/card.tsx";
import {Simulate} from "react-dom/test-utils";


type Props = { namespace: Namespace }
const FeatureTab: FC<Props> = ({namespace}) => {
    const [map, setMap] = useState(new Map([
        ["Schedule", false],
        ["Topic", false],
        ["Webhook", false],
        ["Endpoint", false]
    ]));
    const [updateNamespace, updateResponse, loading, error] = useNamespaceUpdate()
    const [schedule, topic, webhook, endpoint] = map.values()


    function update(updated: Map<string, boolean>) {
        try {
            const actives = new FeatureHotObject()
            updated.forEach((v, k) => {
                if (v) {
                    actives.setFeature(k)
                }
            })
            updateNamespace(namespace.id, namespace.name, actives.get())
        } catch (e) {
            console.log(e)
        }
    }

    function toggle(key: string) {
        const state = map.get(key)
        const updateMap = new Map(map)
        updateMap.set(key, !state)
        update(updateMap)
        setMap(updateMap)

    }

    useEffect(() => {
        if (namespace) {
            const actives = new FeatureHotObject(namespace.features)
            const updateMap = new Map(map)
            for (const i of actives) {
                updateMap.set(i, true)
            }
            setMap(updateMap)
        }

    }, [namespace]);


    if (error) return "Error"

    return (
        <div className="pt-8 px-8">
            <div className={"flex flex-col gap-4"}>
                <FeatureCard
                    label={"Schedule"}
                    description={"All kinds of timers"}
                    icon={<Timer/>}
                    checked={schedule}
                    onCheck={() => toggle("Schedule")}
                />
                <FeatureCard
                    label={"Topic"}
                    description={"Stay tuned"}
                    icon={<Layers3/>}
                    checked={topic}
                    onCheck={() => toggle("Topic")}

                />
                <FeatureCard
                    label={"Webhook"}
                    description={"Stay tuned"}
                    icon={<Webhook/>}
                    checked={webhook}
                    onCheck={() => toggle("Webhook")}

                />
                <FeatureCard
                    label={"Endpoint"}
                    description={"API yourself"}
                    icon={<Globe/>}
                    checked={endpoint}
                    onCheck={() => toggle("Endpoint")}
                />
            </div>
        </div>
    )
}
export default FeatureTab


