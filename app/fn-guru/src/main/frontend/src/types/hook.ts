export interface HookCreateSubmitted {
    id: string;
    hookId: string;
    flowId: string;
    groupId: string;
}

export interface HookList {
    hooks: Array<HookListItem>
}

export interface HookListItem {
    id: string;
    name: string;
    flow: {
        id: string;
        name: string
    },
}

export interface Hook {
    id: string;
    name: string;
    flow: {
        id: string;
        name: string
    },
}