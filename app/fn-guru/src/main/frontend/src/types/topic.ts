export interface TopicCreateRequested {
    id: string;
    status: string;
    topicId: string;
    groupId: string;
}

export type TopicType = 'Internal' | 'Group' | 'Public'

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
