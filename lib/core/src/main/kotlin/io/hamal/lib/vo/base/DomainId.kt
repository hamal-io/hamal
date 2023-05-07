package io.hamal.lib.vo.base

import io.hamal.lib.ddd.base.ValueObject
import io.hamal.lib.util.SnowflakeId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable
abstract class DomainId : ValueObject.ComparableImpl<SnowflakeId>() {
    fun partition() = value.partition()
    fun sequence() = value.sequence()
    fun elapsed() = value.elapsed()
    override fun toString(): String {
        return "${this::class.simpleName}(${value.value})"
    }
}

abstract class DomainIdSerializer<ID : DomainId>(
    val fn: (SnowflakeId) -> ID
) : KSerializer<ID> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Id", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ID {
        return fn(SnowflakeId(decoder.decodeLong()))
    }

    override fun serialize(encoder: Encoder, value: ID) {
        encoder.encodeLong(value.value.value)
    }
}
