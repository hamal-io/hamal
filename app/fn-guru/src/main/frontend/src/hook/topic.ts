import {useCallback} from "react";
import {useGet, usePost} from "@/hook/http.ts";

import {useAuth} from "@/hook/auth.ts";
import {Topic, TopicCreateRequested, TopicList, TopicType} from "@/types/topic.ts";

type TopicGetAction = (topicId: string, abortController?: AbortController) => void
export const useTopicGet = (): [TopicGetAction, Topic, boolean, Error] => {
    const [auth] = useAuth()
    const [get, topic, loading, error] = useGet<Topic>()
    const fn = useCallback(async (topicId: string, abortController?: AbortController) => get(`/v1/topics/${topicId}`, abortController), [auth])
    return [fn, topic, loading, error]
}


type TopicListQuery = {
    namespaceId: string
}

type TopicListAction = (query: TopicListQuery, abortController?: AbortController) => void
export const useTopicList = (): [TopicListAction, TopicList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, topicList, loading, error] = useGet<TopicList>()
    const fn = useCallback(async (query: TopicListQuery, abortController?: AbortController) => get(`/v1/topics`, abortController), [auth])
    return [fn, topicList, loading, error]
}

type TopicCreateAction = (namespaceId: string, name: string, type: TopicType, abortController?: AbortController) => void
export const useTopicCreate = (): [TopicCreateAction, TopicCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TopicCreateRequested>()
    const fn = useCallback(async (namespaceId: string, name: string, type: TopicType, abortController?: AbortController) =>
        post(`/v1/namespaces/${namespaceId}/topics`, {
            name,
            type
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}