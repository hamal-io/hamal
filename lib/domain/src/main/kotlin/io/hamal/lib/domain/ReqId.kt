package io.hamal.lib.domain

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ReqId.Serializer::class)
class ReqId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    internal object Serializer : DomainIdSerializer<ReqId>(::ReqId)
}