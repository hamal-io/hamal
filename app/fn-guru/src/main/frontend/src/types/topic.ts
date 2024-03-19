export interface TopicCreateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
    workspaceId: string;
    namespaceId: string;
    type: string;
}

export type TopicType = 'Namespace' | 'Workspace' | 'Public'

export interface Topic {
    id: string;
    name: string;
    type: TopicType;
}

export interface TopicList {
    topics: Array<TopicListItem>;
}

export interface TopicListItem {
    id: string;
    name: string;
    type: TopicType;
}

export interface TopicEventAppendRequested {
    requestId: string;
    requestStatus: string;
    id: string;
}

export interface TopicEventPayload {
    payload: string
}