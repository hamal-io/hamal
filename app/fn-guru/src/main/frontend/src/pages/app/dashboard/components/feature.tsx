import React, {FC, useEffect, useState} from "react";
import {useNamespaceUpdate} from "@/hook";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {Namespace} from "@/types";
import {FeatureCard} from "@/pages/app/dashboard/components/feature-components/card.tsx";


type Props = { namespace: Namespace }
const FeatureTab: FC<Props> = ({namespace}) => {
    const [updateNamespace, updateRequested, loading, error] = useNamespaceUpdate()
    const [features, setFeatures] = useState(namespace.features)
    const [schedule, topic, webhook, endpoint] = features


    function update() {
        try {
            updateNamespace(namespace.id, namespace.name, features)
        } catch (e) {
            console.log(e)
        }
    }

    function toggle(value: number) {
        const copy = [...features]
        const f = copy.find((x) => x.value === value)
        if (f) {
            f.toggle()
        }
        setFeatures(copy)
        update()
    }


    if (error) return "Error"

    return (
        <div className="pt-8 px-8">
            <div className={"flex flex-col gap-4"}>
                <FeatureCard
                    label={"Schedule"}
                    description={"All kinds of timers"}
                    icon={<Timer/>}
                    checked={schedule.state}
                    onCheck={() => toggle(schedule.value)}
                />
                <FeatureCard
                    label={"Topic"}
                    description={"Stay tuned"}
                    icon={<Layers3/>}
                    checked={topic.state}
                    onCheck={() => toggle(topic.value)}

                />
                <FeatureCard
                    label={"Webhook"}
                    description={"Stay tuned"}
                    icon={<Webhook/>}
                    checked={webhook.state}
                    onCheck={() => toggle(webhook.value)}

                />
                <FeatureCard
                    label={"Endpoint"}
                    description={"API yourself"}
                    icon={<Globe/>}
                    checked={endpoint.state}
                    onCheck={() => toggle(endpoint.value)}
                />
            </div>
        </div>
    )
}
export default FeatureTab


