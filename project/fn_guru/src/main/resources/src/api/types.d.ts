interface ApiSubmittedFunctionCreation {
    reqId: string;
    status: string;
    id: string;
}

interface ApiSubmittedFunctionUpdating {
    reqId: string;
    status: string;
    id: string;
}

export interface ApiSimpleFunction {
    id: string;
    name: string;
}

export interface ApiListFunctions {
    funcs: Array<ApiSimpleFunction>;
}

export interface ApiFunction {
    id: string;
    name: string;
    inputs: any;
    code: {
        value: string
    }
}
