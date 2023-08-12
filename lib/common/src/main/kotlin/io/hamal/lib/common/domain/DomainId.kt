package io.hamal.lib.common.domain

import io.hamal.lib.common.SnowflakeId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
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
    override val descriptor = PrimitiveSerialDescriptor("Id", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ID {
        return fn(SnowflakeId(decoder.decodeString().toLong(16)))
    }

    override fun serialize(encoder: Encoder, value: ID) {
        encoder.encodeString(value.value.value.toString(16))
    }
}
