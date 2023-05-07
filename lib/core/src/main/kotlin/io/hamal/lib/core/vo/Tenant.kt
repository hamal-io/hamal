package io.hamal.lib.core.vo

import io.hamal.lib.core.util.SnowflakeId
import io.hamal.lib.core.vo.base.DomainId
import io.hamal.lib.core.vo.base.DomainIdSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TenantId.Serializer::class)
class TenantId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<TenantId>(::TenantId)
}