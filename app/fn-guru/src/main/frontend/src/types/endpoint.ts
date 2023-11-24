export interface EndpointCreateSubmitted {
    id: string;
    endpointId: string;
    flowId: string;
    groupId: string;
}

export interface EndpointList {
    endpoints: Array<EndpointListItem>
}

export interface EndpointListItem {
    id: string;
    name: string;
    flow: {
        id: string;
        name: string
    },
}

export interface Endpoint {
    id: string;
    name: string;
    flow: {
        id: string;
        name: string
    },
}