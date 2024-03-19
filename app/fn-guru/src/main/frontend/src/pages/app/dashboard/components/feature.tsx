import React, {cloneElement, FC, useEffect, useState} from "react";
import {useNamespaceUpdate} from "@/hook";
import {Namespace, NamespaceFeature} from "@/types";
import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {Card, CardDescription, CardTitle} from "@/components/ui/card.tsx";
import {Avatar} from "@/components/ui/avatar.tsx";
import {Switch} from "@/components/ui/switch.tsx";

type Props = { namespace: Namespace }
const FeatureTab: FC<Props> = ({namespace}) => {
    const [updateNamespace, updateResponse, loading, error] = useNamespaceUpdate()

    const [clientFeatures, setClientFeatures] = useState<NamespaceFeature>({
        schedule: false,
        topic: false,
        webhook: false,
        endpoint: false
    })
    const [updateReady, setUpdateReady] = useState(false)

    useEffect(() => {
        const copy = {...clientFeatures}
        for (const [k] of Object.entries(namespace.features)) {
            copy[k] = true
        }
        setClientFeatures(copy)
    }, []);


    useEffect(() => {
        if (updateReady === true) {
            try {
                const dto: NamespaceFeature = {}
                for (const [k, v] of Object.entries(clientFeatures)) {
                    if (v) {
                        dto[k] = true
                    }
                }
                const abortController = new AbortController()
                updateNamespace(namespace.id, namespace.name, dto, abortController)
                return (() => abortController.abort())
            } catch (e) {
                console.log(e)
            } finally {
                setUpdateReady(false)
            }
        }
    }, [updateReady, clientFeatures]);

    function handleChange(name: string) {
        setClientFeatures(prevState => ({...prevState, [name]: !prevState[name]}));
        setUpdateReady(true)
    }


    if (error) return "Error"


    return (
        <div className="pt-8 px-8">
            <div className={"flex flex-col gap-4"}>
                <FeatureCard
                    name={"schedule"}
                    label={"Schedule"}
                    description={"All kinds of timers"}
                    checked={clientFeatures.schedule}
                    onChange={handleChange}
                />
                <FeatureCard
                    name={"topic"}
                    label={"Topic"}
                    description={"Stay tuned"}
                    checked={clientFeatures.topic}
                    onChange={handleChange}

                />
                <FeatureCard
                    name={"webhook"}
                    label={"Webhook"}
                    description={"Stay tuned"}
                    checked={clientFeatures.webhook}
                    onChange={handleChange}

                />
                <FeatureCard
                    name={"endpoint"}
                    label={"Endpoint"}
                    description={"API yourself"}
                    checked={clientFeatures.endpoint}
                    onChange={handleChange}
                />
            </div>
        </div>
    )
}
export default FeatureTab


type CardProps = {
    name: string
    label: string,
    description: string,
    onChange: (name: string) => void
    checked: boolean
}
export const FeatureCard: FC<CardProps> = ({name, label, description, onChange, checked}) => {

    return (
        <Card className={"flex flex-row items-center  p-4"}>
            <Avatar className={"w-1/4"}></Avatar>
            <div className={"flex flex-col w-1/3"}>
                <CardTitle>{label}</CardTitle>
                <CardDescription>{description}</CardDescription>
            </div>
            <div>
                <Switch name={name} checked={checked} onCheckedChange={() => onChange(name)}></Switch>
            </div>
        </Card>
    )
}