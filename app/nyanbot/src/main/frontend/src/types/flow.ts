export interface Flow {
    id: string
    name: string
    status: string
    funcId?: string
    triggerId?: string
}

export type FlowList = Array<Flow>
