export interface HookCreateRequested {
    id: string;
    hookId: string;
    namespaceId: string;
    groupId: string;
}

export interface HookList {
    hooks: Array<HookListItem>
}

export interface HookListItem {
    id: string;
    name: string;
    namespace: {
        id: string;
        name: string
    },
}

export interface Hook {
    id: string;
    name: string;
    namespace: {
        id: string;
        name: string
    },
}