import {Globe, Layers3, Timer, Webhook} from "lucide-react";
import {createElement, ReactElement} from "react";

export interface NamespaceCreateRequested {
    id: string;
    status: string;
    namespaceIdId: string;
    workspaceId: string;
}


export interface NamespaceAppendRequested {
    id: string;
    status: string;
    namespaceIdId: string;
    workspaceId: string;
}

export interface NamespaceUpdateRequested {
    id: string;
    status: string;
    namespaceIdId: string;
}

export interface Namespace {
    id: string;
    name: string;
}

export interface NamespaceList {
    namespaces: Array<NamespaceListItem>;
}

export interface NamespaceListItem {
    id: string;
    parentId: string;
    name: string;
}


export type NamespaceFeature = {
    value: number
    label: string
    active?: boolean
    description?: string
    icon?: ReactElement
}

export const NamespaceFeatures = new Array<NamespaceFeature>(
    {value: 0, label: "Schedules", description: "lalala", icon: createElement(Timer)},
    {value: 1, label: "Topics", description: "lalala", icon: createElement(Layers3)},
    {value: 2, label: "Webhooks", description: "lalala", icon: createElement(Webhook)},
    {value: 3, label: "Endpoints", description: "lalala", icon: createElement(Globe)},
)