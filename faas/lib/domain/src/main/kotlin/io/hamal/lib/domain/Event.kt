package io.hamal.lib.domain

import io.hamal.lib.domain.vo.TopicEntryId
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

// FIXME separate topic from payload
@Serializable(with = Event.Serializer::class)
class Event(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<Event>(::Event) // FIXME custom event serializer
}


@Serializable
data class EventWithId(
    val id: TopicEntryId,
    val value: MapType
)