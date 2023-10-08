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


export interface ApiExecList {
    execs: Array<ApiExecSimple>;
}

export interface ApiExecSimple {
    id: string;
    status: string;
}
