package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import kotlinx.serialization.Serializable

@Serializable(with = HookId.Serializer::class)
class HookId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : DomainIdSerializer<HookId>(::HookId)
}


@Serializable(with = HookName.Serializer::class)
class HookName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<HookName>(::HookName)
}