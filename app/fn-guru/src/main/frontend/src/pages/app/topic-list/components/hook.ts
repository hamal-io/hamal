import {TopicListItem} from "@/types/topic.ts";
import {FuncListItem} from "@/types";
import {useTriggerListEvent} from "@/hook";
import {useTopicList} from "@/hook/topic.ts";
import {useCallback, useEffect, useState} from "react";

export type TopicWithFuncs = {
    topic: TopicListItem
    funcs: Array<FuncListItem>
}
type GetTopicsWithFuncsAction = (namespaceId: string, abortController?: AbortController) => void
export const useTopicsWithFuncs = (): [GetTopicsWithFuncsAction, Array<TopicWithFuncs>, boolean, Error] => {
    const [listTriggers, triggerList, , triggersError] = useTriggerListEvent()
    const [listTopics, topicList, , topicsError] = useTopicList()
    const [topicsWithFuncs, setTopicWithFuncs] = useState<Array<TopicWithFuncs>>(null)
    const [loading, setLoading] = useState(true)

    const fn = useCallback<GetTopicsWithFuncsAction>(async (namespaceId, abortController?) => {
        try {
            listTopics(namespaceId, abortController)
            listTriggers(namespaceId, abortController)
        } catch (e) {
            console.log(e)
            setLoading(false)
        } finally {
            abortController.abort()
        }
    }, [])


    useEffect(() => {
        if (topicList && triggerList) {
            const x: Array<TopicWithFuncs> = topicList.topics.map(topic => {
                return {
                    topic: topic,
                    funcs: triggerList.triggers.filter(trigger => trigger.topic.id === topic.id)
                }
            })
            setTopicWithFuncs(x)
            setLoading(false)
        }
    }, [topicList, triggerList]);

    return [fn, topicsWithFuncs, loading, triggersError || topicsError]
}