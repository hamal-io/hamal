interface ApiSubmittedAdhocInvocation {
    reqId: string;
    status: string;
    id: string;
}

interface ApiSubmittedFunctionCreation {
    reqId: string;
    status: string;
    id: string;
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

export interface ApiFunction {
    id: string;
    name: string;
    inputs: any;
    code: {
        value: string
    }
}

export interface ApiExecution {
    id: string;
}

interface ApiSubmittedTriggerCreation {
    reqId: string;
    status: string;
    id: string;
}


type TriggerType = "FixedRate" | "Event";

export interface ApiSimpleTrigger {
    id: string;
    name: string;
    type: TriggerType;
}

export interface ApiListTriggers {
    triggers: Array<ApiSimpleTrigger>;
}


export interface ApiTrigger {
    id: string;
    name: string;
    type: TriggerType;
}
