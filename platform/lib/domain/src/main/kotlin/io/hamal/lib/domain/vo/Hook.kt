package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

@Serializable(with = HookId.Serializer::class)
class HookId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : SerializableDomainIdSerializer<HookId>(::HookId)
}

@Serializable(with = HookName.Serializer::class)
class HookName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<HookName>(::HookName)
}

@Serializable(with = HookHeaders.Serializer::class)
class HookHeaders(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<HookHeaders>(::HookHeaders)
}

@Serializable(with = HookParameters.Serializer::class)
class HookParameters(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<HookParameters>(::HookParameters)
}
