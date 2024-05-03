package io.hamal.lib.domain

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


//fun Event.toHot() = JsonObject.builder()
//    .set("id", id.stringValue)
//    .set(
//        "topic", JsonObject.builder()
//            .set("id", topic.id.stringValue)
//            .set("name", topic.name.stringValue)
//            .build()
//    )
//    .set("payload", payload.value)
//    .build()
//
//fun List<Event>.toHot() = JsonArray(this.map { it.toHot() })