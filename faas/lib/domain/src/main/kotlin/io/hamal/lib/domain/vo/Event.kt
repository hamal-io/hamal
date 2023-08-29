package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

@Serializable(with = EventId.Serializer::class)
data class EventId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    internal object Serializer : DomainIdSerializer<EventId>(::EventId)
}



@Serializable(with = EventPayload.Serializer::class)
class EventPayload(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<EventPayload>(::EventPayload)
}


@Serializable(with = TopicId.Serializer::class)
data class TopicId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<TopicId>(::TopicId)
}

@Serializable(with = TopicName.Serializer::class)
class TopicName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<TopicName>(::TopicName)
}
