package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable(with = FuncId.Serializer::class)
class FuncId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : SerializableDomainIdSerializer<FuncId>(::FuncId)
}


@Serializable(with = FuncName.Serializer::class)
class FuncName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<FuncName>(::FuncName)
}

@Serializable(with = FuncInputs.Serializer::class)
class FuncInputs(override val value: MapType = MapType()) : MapValueObject() {
    internal object Serializer : InputsSerializer<FuncInputs>(::FuncInputs)
}

@Serializable(with = DeployMessage.Serializer::class)
data class DeployMessage(override val value: String) : StringValueObject() {
    internal object Serializer : StringValueObjectSerializer<DeployMessage>(::DeployMessage)

    companion object {
        val empty = DeployMessage("")
    }
}

@Serializable(with = DeployedAt.Serializer::class)
class DeployedAt(override val value: Instant) : DomainAt() {
    companion object {
        val default = DeployedAt(Instant.ofEpochMilli(1698451200000 /*Happy birthday*/))
    }

    internal object Serializer : DomainAtSerializer<DeployedAt>(::DeployedAt)
}
