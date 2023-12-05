export interface FuncCreateSubmitted {
    id: string;
    status: string;
    funcId: string;
    flowId: string;
    groupId: string;
}

export interface FuncUpdateSubmitted {
    id: string;
    status: string;
    funcId: string;
}


export interface FuncInvokeSubmitted {
    id: string;
    status: string;
    funcId: string;
}

export interface FuncDeploySubmitted {
    id: string;
    status: string;
    funcId: string;
}

export interface Func {
    id: string;
    name: string;
    inputs: object;
    code: {
        id: string;
        version: number;
        value: string;
    },
    deployment: {
        id: string;
        version: number;
        value: string;
        message: string;
    }
}

export interface FuncList {
    funcs: Array<FuncListItem>;
}

export interface FuncListItem {
    id: string;
    name: string;
}
