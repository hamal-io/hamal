package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import kotlinx.serialization.Serializable

@Serializable(with = ReqId.Serializer::class)
class ReqId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<ReqId>(::ReqId)
}