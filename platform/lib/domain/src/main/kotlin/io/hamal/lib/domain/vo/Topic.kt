package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.Map
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

@Serializable(with = TopicId.Serializer::class)
data class TopicId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : SerializableDomainIdSerializer<TopicId>(::TopicId)
}

@Serializable(with = TopicName.Serializer::class)
class TopicName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<TopicName>(::TopicName)
}

@Serializable(with = TopicEntryId.Serializer::class)
data class TopicEntryId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : SerializableDomainIdSerializer<TopicEntryId>(::TopicEntryId)
}

@Serializable(with = TopicEntryPayload.Serializer::class)
class TopicEntryPayload(override val value: MapType = MapType()) : Map() {
    internal object Serializer : InputsSerializer<TopicEntryPayload>(::TopicEntryPayload)
}
