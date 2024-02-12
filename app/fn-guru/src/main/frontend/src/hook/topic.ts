import {useCallback} from "react";
import {useGet, usePatch, usePost} from "@/hook/http.ts";

import {useAuth} from "@/hook/auth.ts";
import {Topic, TopicCreateRequested, TopicList} from "@/types/topic.ts";

type TopicGetAction = (topicId: string, abortController?: AbortController) => void
export const useTopicGet = (): [TopicGetAction, Topic, boolean, Error] => {
    const [auth] = useAuth()
    const [get, topic, loading, error] = useGet<Topic>()
    const fn = useCallback(async (topicId: string, abortController?: AbortController) => get(`/v1/topics/${topicId}`, abortController), [auth])
    return [fn, topic, loading, error]
}

type TopicGroupListAction = (groupId: string, abortController?: AbortController) => void
export const useTopicGroupList = (): [TopicGroupListAction, TopicList, boolean, Error] => {
    const [auth] = useAuth()
    const [get, topicList, loading, error] = useGet<TopicList>()
    const fn = useCallback(async (groupId: string, abortController?: AbortController) => get(`/v1/groups/${groupId}/topics`, abortController), [auth])
    return [fn, topicList, loading, error]
}

type TopicGroupCreateAction = (groupId: string, name: string, abortController?: AbortController) => void
export const useTopicGroupCreate = (): [TopicGroupCreateAction, TopicCreateRequested, boolean, Error] => {
    const [auth] = useAuth()
    const [post, submission, loading, error] = usePost<TopicCreateRequested>()
    const fn = useCallback(async (groupId: string, name: string, abortController?: AbortController) =>
        post(`/v1/groups/${groupId}/topics`, {
            name,
            inputs: {},
            code: ""
        }, abortController), [auth]
    )
    return [fn, submission, loading, error]
}