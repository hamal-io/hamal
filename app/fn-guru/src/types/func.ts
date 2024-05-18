export interface FuncCreateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
    namespaceId: string;
    workspaceId: string;
}

export interface FuncUpdateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
}


export interface FuncInvokeRequested {
    requestId: string;
    requestStatus: string;
    id: string;
}

export interface FuncDeployRequested {
    requestId: string;
    requestStatus: string;
    id: string;
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

