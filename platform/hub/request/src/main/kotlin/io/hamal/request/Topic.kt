package io.hamal.request

import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName


interface CreateTopicReq {
    val name: TopicName
}

interface AppendEntryReq {
    val topicId: TopicId
    val payload: TopicEntryPayload
}