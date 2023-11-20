export interface ExecInvokeSubmitted {
    id: string;
    status: string;
    execId: string;
    groupId: string;
    flowId: string;
}

export interface ExecList {
    execs: Array<ExecListItem>;
}

export interface ExecListItem {
    id: string;
    status: string;
}
