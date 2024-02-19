export interface ExecInvokeRequested {
    id: string;
    status: string;
    execId: string;
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
}

export interface ExecListItemNamespace {
    id: string;
    name: string;
}

export interface ExecListItemInvocation {
    class: string;
}