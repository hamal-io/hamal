import React, {FC, useEffect, useState} from "react";
import {useNamespaceUpdate} from "@/hook";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {Namespace} from "@/types";
import {FeatureCard} from "@/pages/app/dashboard/components/feature-components/card.tsx";
import {useFeature} from "@/pages/app/dashboard/components/feature-components/hook.ts";




type Props = { namespace: Namespace }
const FeatureTab: FC<Props> = ({namespace}) => {
    const [updateNamespace, updateRequested, loading, error] = useNamespaceUpdate()
    const [map, setStates, toggleState, getActive] = useFeature()
    const [schedule, topic, webhook, endpoint] = map.values()

    function update() {
        try {
            updateNamespace(namespace.id, namespace.name, getActive())
        } catch (e) {
            console.log(e)
        }
    }

    function toggle(key: string) {
        toggleState(key)
        update()
    }

    useEffect(() => {
        setStates(namespace.features)
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
                    onCheck={() =>toggle("Topic")}

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


