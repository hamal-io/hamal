package io.hamal.lib.domain

import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.TableType
import kotlinx.serialization.Serializable


@Serializable(with = Event.Serializer::class)
class Event(override val value: TableType = TableType()) : Inputs() {
    internal object Serializer : InputsSerializer<Event>(::Event) // FIXME custom event serializer
}


@Serializable
data class EventWithId(
    val id: EventId,
    val value: TableType
)