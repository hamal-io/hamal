import React, {FC, useEffect, useState} from "react";
import {useNamespaceUpdate} from "@/hook";
import {Namespace, NamespaceFeature} from "@/types";
import {Globe, icons, Layers3, LucideProps, Timer, Webhook} from "lucide-react";
import {Card, CardDescription, CardTitle} from "@/components/ui/card.tsx";
import {Avatar} from "@/components/ui/avatar.tsx";
import {Switch} from "@/components/ui/switch.tsx";

type Props = { namespace: Namespace }
const FeatureTab: FC<Props> = ({namespace}) => {
    const [updateNamespace, updateResponse, loading, error] = useNamespaceUpdate()
    const [clientFeatures, setClientFeatures] = useState<NamespaceFeature>(namespace.features)
    const [updateReady, setUpdateReady] = useState(false)

    useEffect(() => {
        if (updateReady === true) {
            try {
                const abortController = new AbortController()
                updateNamespace(namespace.id, namespace.name, clientFeatures, abortController)
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

    const items = {
        schedule: {
            name: "schedule",
            label: "Schedule",
            icon: Timer,
            description: "All kinds of timers",
            checked: clientFeatures.schedule,
            onChange: handleChange
        },
        topic: {
            name: "topic",
            label: "Topic",
            icon: Layers3,
            description: "Stay tuned",
            checked: clientFeatures.topic,
            onChange: handleChange
        },
        webhook: {
            name: "webhook",
            label: "Webhook",
            icon: Webhook,
            description: "Stay tuned",
            checked: clientFeatures.webhook,
            onChange: handleChange
        },
        endpoint: {
            name: "endpoint",
            label: "Endpoint",
            icon: Globe,
            description: "API yourself",
            checked: clientFeatures.endpoint,
            onChange: handleChange
        }
    }

    return (
        <div className="pt-8 px-8">
            <div className={"flex flex-col gap-4"}>
                <FeatureCard item={items.schedule}/>
                <FeatureCard item={items.topic}/>
                <FeatureCard item={items.webhook}/>
                <FeatureCard item={items.endpoint}/>
            </div>
        </div>
    )
}
export default FeatureTab


type FeatureItem = {
    name: string
    label: string,
    icon: React.ForwardRefExoticComponent<LucideProps>,
    description: string,
    onChange: (name: string) => void
    checked: boolean
}
export const FeatureCard: FC<{ item: FeatureItem }> = ({item}) => {

    return (
        <Card className={"flex flex-row items-center p-4"}>
            <Avatar className={"w-1/4"}>
                <item.icon size={32}/>
            </Avatar>
            <div className={"flex flex-col w-1/3"}>
                <CardTitle>{item.label}</CardTitle>
                <CardDescription>{item.description}</CardDescription>
            </div>
            <div>
                <Switch checked={item.checked} onCheckedChange={() => item.onChange(item.name)}></Switch>
            </div>
        </Card>
    )
}