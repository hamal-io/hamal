interface ApiSubmittedAdhocInvocation {
    id: string;
    status: string;
    execId: string;
}


interface ApiSubmittedFunctionCreation {
    id: string;
    status: string;
    funcId: string;
}


export interface ApiSimpleExecution {
    id: string;
    status: string;
}

export interface ApiListExecutions {
    execs: Array<ApiSimpleExecution>;
}

export interface ApiSimpleFunction {
    id: string;
    name: string;
}

export interface ApiListFunctions {
    funcs: Array<ApiSimpleFunction>;
}