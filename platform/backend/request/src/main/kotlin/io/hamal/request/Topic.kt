package io.hamal.request

import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName


interface TopicCreateReq {
    val name: TopicName
}

interface TopicAppendEntryReq {
    val topicId: TopicId
    val payload: TopicEntryPayload
}