package io.hamal.lib.domain

import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName


data class EventToSubmit(
    val topicName: TopicName,
    val payload: EventPayload
)

data class Event(
    val topic: EventTopic,
    val id: EventId,
    val payload: EventPayload
)

data class EventTopic(
    val id: TopicId,
    val name: TopicName
)


fun Event.toHot() = HotObject.builder()
    .set("id", id.value.value.toString(16))
    .set(
        "topic", HotObject.builder()
            .set("id", topic.id.value.value.toString(16))
            .set("name", topic.name.value)
            .build()
    )
    .set("payload", payload.value)
    .build()

fun List<Event>.toHot() = HotArray(this.map { it.toHot() })