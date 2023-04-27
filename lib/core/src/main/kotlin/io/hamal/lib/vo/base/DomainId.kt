package io.hamal.lib.vo.base

import io.hamal.lib.ddd.base.ValueObject
import io.hamal.lib.util.Snowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class DomainId : ValueObject.ComparableImpl<Snowflake.Id>()

abstract class DomainIdSerializer<ID : DomainId>(
    val fn: (Snowflake.Id) -> ID
) : KSerializer<ID> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Id", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ID {
        return fn(Snowflake.Id(decoder.decodeLong()))
    }

    override fun serialize(encoder: Encoder, value: ID) {
        encoder.encodeLong(value.value.value)
    }
}
