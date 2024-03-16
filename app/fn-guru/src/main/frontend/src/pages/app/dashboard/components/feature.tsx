import React, {useEffect} from "react";
import {useUiState} from "@/hook/ui-state.ts";
import {useNamespaceGet, useNamespaceUpdate} from "@/hook";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {FeatureObject} from "@/types";
import {useFeatures} from "@/pages/app/dashboard/components/feature-components/hook.ts";
import {FeatureCard} from "@/pages/app/dashboard/components/feature-components/card.tsx";


const NamespaceDetailPage = () => {
    const [uiState] = useUiState()
    const [getNamespace, namespace, loading, error] = useNamespaceGet()
    const [updateNamespace, updateRequested, loading2, error2] = useNamespaceUpdate()
    const [fetchFeatures, toggleFeature, features] = useFeatures()
    const [schedule, topic, webhook, endpoint] = features

    useEffect(() => {
        const abortController = new AbortController()
        getNamespace(uiState.namespaceId, abortController)
        return (() => abortController.abort())
    }, [uiState]);

    useEffect(() => {
        if (namespace) {
            fetchFeatures(namespace)
        }
    }, [namespace]);


    function updateFeatures() {
        try {
            const answer: FeatureObject = {}
            features.forEach((f) => {
                if (f.state === true) {
                    answer[f.name] = f.value
                }
            })
            updateNamespace(uiState.namespaceId, namespace.name, answer)

        } catch (e) {
            console.log(e)
        }
    }

    function toggle(value: number) {
        toggleFeature(value)
        updateFeatures()
    }


    if (error) return "Error"
    if (loading) return "Loading..."
    return (
        <div className="pt-8 px-8">
            <div className={"flex flex-col gap-4"}>
                <FeatureCard
                    label={"Schedules"}
                    description={"All kinds of timers"}
                    icon={<Timer/>}
                    checked={schedule.state}
                    onCheck={() => toggle(schedule.value)}
                />
                <FeatureCard
                    label={"Topics"}
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
export default NamespaceDetailPage


