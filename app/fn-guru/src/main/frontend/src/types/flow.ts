export interface FlowCreateSubmitted {
    id: string;
    status: string;
    flowId: string;
    groupId: string;
}

export interface Flow {
    id: string;
    name: string;
}

export interface FlowList {
    flows: Array<FlowListItem>;
}

export interface FlowListItem {
    id: string;
    name: string;
}