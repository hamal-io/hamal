export interface Flow {
    id: string
    name: string
    status: string
}

export interface FlowList {
    flows: Array<Flow>
}