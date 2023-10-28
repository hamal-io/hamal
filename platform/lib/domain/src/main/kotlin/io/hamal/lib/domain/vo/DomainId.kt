package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.snowflake.SnowflakeId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable
sealed class SerializableDomainId : DomainId() {
    override fun partition() = value.partition()
    override fun sequence() = value.sequence()
    override fun elapsed() = value.elapsed()
    override fun toString(): String {
        return "${this::class.simpleName}(${value.value})"
    }
}

abstract class SerializableDomainIdSerializer<ID : DomainId>(
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
