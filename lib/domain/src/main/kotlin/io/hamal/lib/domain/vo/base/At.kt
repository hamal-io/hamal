@file:OptIn(ExperimentalSerializationApi::class)

package io.hamal.lib.domain.vo.base

import io.hamal.lib.domain.ddd.base.ValueObject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

abstract class DomainAt : ValueObject.ComparableImpl<Instant>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class DomainAtSerializer<AT : DomainAt>(
    val fn: (Instant) -> AT
) : KSerializer<AT> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Id", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): AT {
        return fn(InstantSerializer.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: AT) {
        InstantSerializer.serialize(encoder, value.value)
    }
}


@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeLong(value.toEpochMilli())
    override fun deserialize(decoder: Decoder) = Instant.ofEpochMilli(decoder.decodeLong())
}