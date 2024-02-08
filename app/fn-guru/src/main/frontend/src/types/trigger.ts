export interface TriggerCreateSubmitted {
    id: string;
    status: string;
    triggerId: string;
    funcId: string;
    namespaceId: string;
    groupId: string;
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
        id: string,
        name: string,
        methods: Array<String>
    }
}