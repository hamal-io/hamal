import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {createElement, ReactElement} from "react";

export type NamespaceFeature = {
    value: number
    label: string
    active?: boolean
    description?: string
    icon?: ReactElement
}


export const NamespaceFeatures = new Array<NamespaceFeature>(
    {value: 0, label: "Schedules", description: "lalala",icon:  createElement(Timer)},
    {value: 1, label: "Topics", description: "lalala",icon: createElement(Layers3)},
    {value: 2, label: "Webhooks", description: "lalala",icon: createElement(Webhook)},
    {value: 3, label: "Endpoints", description: "lalala",icon: createElement(Globe)},
)