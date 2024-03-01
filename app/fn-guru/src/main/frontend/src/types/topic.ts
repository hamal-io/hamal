export interface TopicCreateRequested {
    id: string;
    status: string;
    topicId: string;
    workspaceId: string;
}

export type TopicType = 'Internal' | 'Namespace' | 'Workspace' | 'Public'

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
