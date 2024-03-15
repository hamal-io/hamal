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

export interface FeatureObject {
    [key: string]: number
}

export interface Namespace {
    id: string;
    name: string;
    features: FeatureObject;
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



