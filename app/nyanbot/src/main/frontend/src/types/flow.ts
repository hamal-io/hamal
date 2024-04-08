interface Flow {
    id: string
    name: string
}

interface FlowCreateRequested {
    id: string,
    name: string
}

interface FlowList {
    flows: Array<Flow>
}

