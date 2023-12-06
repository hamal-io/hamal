package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.DomainNameSerializer
import io.hamal.lib.common.domain.StringValueObject
import io.hamal.lib.common.domain.StringValueObjectSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

@Serializable(with = FlowId.Serializer::class)
class FlowId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : SerializableDomainIdSerializer<FlowId>(::FlowId)

    companion object {
        val root = FlowId(1)
    }
}


@Serializable(with = FlowName.Serializer::class)
class FlowName(override val value: String) : DomainName() {
    companion object {
        val default = FlowName("__default__")
    }

    internal object Serializer : DomainNameSerializer<FlowName>(::FlowName)
}

@Serializable(with = FlowInputs.Serializer::class)
class FlowInputs(override val value: MapType = MapType()) : MapValueObject() {
    internal object Serializer : InputsSerializer<FlowInputs>(::FlowInputs)
}

@Serializable(with = FlowType.Serializer::class)
class FlowType(override val value: String) : StringValueObject() {
    companion object {
        val default = FlowType("__default__")
    }

    internal object Serializer : StringValueObjectSerializer<FlowType>(::FlowType)
}