
export interface NamespaceAppendRequested {
    requestId: string;
    requestStatus: string;
    id: string;
    workspaceId: string;
}

export interface NamespaceUpdateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
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

}
export const NamespaceFeatures = new Array<NamespaceFeature>(
    {value: 0, label: "Schedules", description: "All kinds of timers", icon: createElement(Timer)},
    {value: 1, label: "Topics", description: "Stay tuned", icon: createElement(Layers3)},
    {value: 2, label: "Webhooks", description: "Stay tuned", icon: createElement(Webhook)},
    {value: 3, label: "Endpoints", description: "API yourself", icon: createElement(Globe)},
)