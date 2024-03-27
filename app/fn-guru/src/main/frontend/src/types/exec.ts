export interface ExecInvokeRequested {
    requestedId: string;
    requestStatus: string;
    id: string;
    workspaceId: string;
    namespaceId: string;
}

export interface ExecList {
    execs: Array<ExecListItem>;
}

export interface ExecListItem {
    id: string;
    status: string;
    namespace: ExecListItemNamespace;
    invocation: ExecListItemInvocation;
    correlation?: string;
    func?: ExecListItemFunc;
    trigger?: ExecTriggerItem;
}

export interface ExecListItemNamespace {
    id: string;
    name: string;
}

export interface ExecListItemInvocation {
    class: string;
}

export interface ExecListItemFunc {
    id: string;
    name: string;
}

export interface Exec {
    id: string;
    status: string;
    func?: {
        id: string;
        name: string;
    };
    correlation: string;
    result?: object;
    state?: object;
}