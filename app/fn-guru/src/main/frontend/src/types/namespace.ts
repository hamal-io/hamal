export interface NamespaceCreateRequested {
    id: string;
    status: string;
    namespaceIdId: string;
    workspaceId: string;
}


export interface NamespaceAppendRequested {
    id: string;
    status: string;
    namespaceIdId: string;
    workspaceId: string;
}

export interface NamespaceUpdateRequested {
    id: string;
    status: string;
    namespaceIdId: string;
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
    parentId: string;
    name: string;
}

export interface NamespaceView {
    root: NamespaceListItem
    children: Array<NamespaceListItem>
}
