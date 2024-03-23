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
    func: {
        id: string;
        name: string;
    },
    hook?: {
        id: string;
        name: string;
        method: string;
    },
    topic?: {
        id: string;
        name: string;
    },

}