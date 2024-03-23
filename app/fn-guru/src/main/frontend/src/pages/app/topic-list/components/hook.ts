import {TopicListItem} from "@/types/topic.ts";
import {FuncListItem} from "@/types";
import {useTriggerListEvent} from "@/hook";
import {useTopicList} from "@/hook/topic.ts";
import {useCallback, useEffect, useState} from "react";

export type TopicWithFuncs = {
    topic: TopicListItem
    funcs: Array<FuncListItem>
}
type GetTopicsWithFuncsAction = (namespaceId: string) => void
export const useTopicsWithFuncs = (): [GetTopicsWithFuncsAction, Array<TopicWithFuncs>, boolean, Error] => {
    const [listTriggers, triggerList, , triggersError] = useTriggerListEvent()
    const [listTopics, topicList, , topicsError] = useTopicList()
    const [topicsWithFuncs, setTopicWithFuncs] = useState<Array<TopicWithFuncs>>(null)
    const [loading, setLoading] = useState(true)

    const handleRequest = (ns: string) => {
        const abortController = new AbortController()
        listTopics(ns, abortController)
        listTriggers(ns, abortController)
        return (() => abortController.abort())
    }

    const fn = useCallback<GetTopicsWithFuncsAction>(async (namespaceId) => {
        try {
            handleRequest(namespaceId)
        } catch (e) {
            console.log(e)
            setLoading(false)
        }
    }, [])


    useEffect(() => {
        if (topicList && triggerList) {
            setTopicWithFuncs(topicList.topics.map(topic => {
                return {
                    topic: topic,
                    funcs: triggerList.triggers.filter(trigger => trigger.topic.id === topic.id)
                }
            }))
            setLoading(false)
        }
    }, [topicList, triggerList]);

    return [fn, topicsWithFuncs, loading, triggersError || topicsError]
}