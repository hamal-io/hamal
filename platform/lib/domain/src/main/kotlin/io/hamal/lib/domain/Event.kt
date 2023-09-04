package io.hamal.lib.domain

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable


@Serializable
data class EventToSubmit(
    val topicName: TopicName,
    val payload: EventPayload
)

@Serializable
data class Event(
    val topic: EventTopic,
    val id: EventId,
    val payload: EventPayload
)

@Serializable
data class EventTopic(
    val id: TopicId,
    val name: TopicName
)


@Serializable(with = EventId.Serializer::class)
data class EventId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    internal object Serializer : DomainIdSerializer<EventId>(::EventId)
}

@Serializable(with = EventPayload.Serializer::class)
class EventPayload(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<EventPayload>(::EventPayload)
}