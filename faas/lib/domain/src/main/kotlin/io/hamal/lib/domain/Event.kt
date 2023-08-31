package io.hamal.lib.domain

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
    val payload: EventPayload
)

@Serializable(with = EventPayload.Serializer::class)
class EventPayload(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<EventPayload>(::EventPayload)
}


