export interface ExecInvokeRequested {
    id: string;
    status: string;
    execId: string;
    groupId: string;
    namespaceId: string;
}

export interface ExecList {
    execs: Array<ExecListItem>;
}

export interface ExecListItem {
    id: string;
    status: string;
}
