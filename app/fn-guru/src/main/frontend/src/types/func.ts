export interface FuncCreateRequested {
    id: string;
    status: string;
    funcId: string;
    namespaceId: string;
    groupId: string;
}

export interface FuncUpdateRequested {
    id: string;
    status: string;
    funcId: string;
}


export interface FuncInvokeRequested {
    id: string;
    status: string;
    funcId: string;
}

export interface FuncDeployRequested {
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

export interface FuncDeploymentList {
    deployments: Array<Deployment>
}

export interface Deployment {
    version: string,
    message: string,
    deployedAt: string
}

