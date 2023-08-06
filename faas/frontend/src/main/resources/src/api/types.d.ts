interface ApiSubmittedAdhocInvocation {
    id: string;
    status: string;
    execId: string;
}

export interface ApiSimpleExecution {
    id: string,
    status: string
}

export interface ApiListExecution {
    execs: Array<ApiSimpleExecution>
}