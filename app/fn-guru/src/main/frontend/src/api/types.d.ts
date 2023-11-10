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
        id: string;
        current: VersionedCode;
        deployed: VersionedCode;
    }
}

interface VersionedCode {
    version: number;
    value: string;
}

export interface ApiFlowList {
    flows: Array<ApiFlowsimple>;
}

export interface ApiFlowsimple {
    id: string;
    name: string;
}

export interface ApiFlow {
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