interface Flow {
    id: string
    name: string
    status: string
}

interface FlowCreateRequested {
    id: string,
    name: string,
    status: string
}

interface FlowList {
    flows: Array<Flow>
}

