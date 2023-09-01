import {defaultHeaders} from "./shared";

export interface ListTopicsQuery {
    limit: number;
}

export interface ApiSimpleTopic {
    id: string;
    name: string;
}

export interface ApiTopic {
    id: string;
    name: string;
}


export interface ApiListTopics {
    funcs: Array<ApiSimpleTopic>;
}

export async function listTopics(query: ListTopicsQuery): Promise<ApiListTopics> {
    const response = await fetch("http://localhost:8008/v1/topics", {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiListTopics;
}

export async function getTopic(topicId: string): Promise<ApiTopic> {
    const response = await fetch(`http://localhost:8008/v1/topics/${topicId}`, {
        headers: defaultHeaders,
        method: "GET",
    })
    if (!response.ok) {
        const message = `Request submission failed: ${response.status} - ${response.statusText}`;
        throw new Error(message);
    }
    return await response.json() as ApiTopic;
}