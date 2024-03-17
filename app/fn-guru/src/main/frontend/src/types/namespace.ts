
export interface NamespaceAppendRequested {
    requestId: string;
    requestStatus: string;
    id: string;
    workspaceId: string;
}

export interface NamespaceUpdateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
}


export class Feature {
    name: string
    value: number
    state: boolean

    constructor(name: string, value: number) {
        this.name = name;
        this.value = value;
        this.state = false
    }

    toggle() {
        this.state = !this.state
    }
}

export const features = new Array<Feature>(
    new Feature("Schedules", 0),
    new Feature("Topics", 1),
    new Feature("Webhooks", 2),
    new Feature("Endpoints", 3)
)

export interface Namespace {
    id: string;
    name: string;
    features: Array<Feature>;
}

export interface NamespaceList {
    namespaces: Array<NamespaceListItem>;
}

export interface NamespaceListItem {
    id: string;
    parentId: string;
    name: string;
}

export class NamespaceNode {
    data: NamespaceListItem
    descendants: Array<NamespaceNode>

    constructor(value: NamespaceListItem) {
        this.data = value
        this.descendants = []
    }

    addDescendant(other: NamespaceNode) {
        if (this.data.id !== other.data.id) {
            this.descendants.push(other)
        }
    }

    isParent() {
        return this.descendants.length > 0
    }
}



