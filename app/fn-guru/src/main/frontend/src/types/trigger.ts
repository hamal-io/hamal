export interface TriggerCreateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
    funcId: string;
    namespaceId: string;
    workspaceId: string;
}

export interface TriggerList {
    triggers: Array<TriggerListItem>
}

export interface TriggerListItem {
    id: string;
    type: string;
    name: string;
    status: string;
    func: {
        id: string;
        name: string;
    },
    hook?: {
        id: string;
        name: string;
    },
    topic?: {
        id: string;
        name: string;
    }
}

export interface TriggerStatusRequested {
    "requestId": string,
    "requestStatus": string,
    "id": string,
    "status": string,
}
