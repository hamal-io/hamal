export interface Flow {
    id: string
    name: string
    status?: string
    funcId?: string
    triggerId?: string
}

export interface FlowCreateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
    workspaceId: string;
}


export type FlowList = Array<Flow>
