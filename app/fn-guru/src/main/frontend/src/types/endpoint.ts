export interface EndpointCreateRequested {
    requestId: string;
    id: string;
    namespaceId: string;
    workspaceId: string;
}

export interface EndpointList {
    endpoints: Array<EndpointListItem>
}

export interface EndpointListItem {
    id: string;
    name: string;
    namespace: {
        id: string;
        name: string
    },
}

export interface Endpoint {
    id: string;
    name: string;
    namespace: {
        id: string;
        name: string
    },
}