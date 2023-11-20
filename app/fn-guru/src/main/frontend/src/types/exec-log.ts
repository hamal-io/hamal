export interface ExecLogList {
    logs: Array<ExecLogListItem>
}

export interface ExecLogListItem {
    id: string;
    execId: string;
    level: string,
    message: string,
    localAt: number,
    remoteAt: number
}
