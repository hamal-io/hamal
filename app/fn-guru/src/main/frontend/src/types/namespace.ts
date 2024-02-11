export interface NamespaceCreateRequested {
    id: string;
    status: string;
    namespaceIdId: string;
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