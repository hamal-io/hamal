export interface NamespaceCreateSubmitted {
    id: string;
    status: string;
    namespaceId: string;
    groupId: string;
}

export interface DefaultNamespaceId {
    namespaceId: string;
    groupId: string;
}

export interface Namespace {
    id: string;
    name: string;
}

export interface NamespaceList {
    namespaces: Array<NamespaceListItem>;
}

export interface NamespaceListItem {
    id: string;
    name: string;
}