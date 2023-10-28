interface ApiSubmittedReqWithId {
    reqId: string;
    status: string;
    id: string;
}

export interface ApiFuncSimple {
    id: string;
    name: string;
}


export interface ApiFuncList {
    funcs: Array<ApiFuncSimple>;
}

export interface ApiFunc {
    id: string;
    name: string;
    inputs: object;
    code: {
        value: string
    }
}

export interface ApiNamespaceList {
    namespaces: Array<ApiNamespaceSimple>;
}

export interface ApiNamespaceSimple {
    id: string;
    name: string;
}

export interface ApiNamespace {
    id: string;
    name: string;
}

export interface ApiExecList {
    execs: Array<ApiExecSimple>;
}

export interface ApiExecSimple {
    id: string;
    status: string;
}

export interface ApiExecLogList {
    logs: Array<ApiExecLog>;
}

type ExecLogLevel = 'Info' | 'Warn'

export interface ApiExecLog {
    id: string;
    level: ExecLogLevel,
    message: string;
    localAt: number;
    remoteAt: number;
}

type ApiExecStatus = 'Completed' | 'Failed'

export interface ApiExec {
    id: string;
    status: ApiExecStatus
    code: ApiExecCode
    result?: object
}

export interface ApiExecCode {
    id: string;
    version: number;
    value: string;
}