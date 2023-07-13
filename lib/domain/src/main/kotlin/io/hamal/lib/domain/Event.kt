package io.hamal.lib.domain

import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.script.api.value.TableValue
import kotlinx.serialization.Serializable


@Serializable(with = Event.Serializer::class)
class Event(override val value: TableValue = TableValue()) : Inputs() {
    internal object Serializer : InputsSerializer<Event>(::Event)
}


@Serializable
data class EventWithId(
    val id: EventId,
    val value: TableValue
)