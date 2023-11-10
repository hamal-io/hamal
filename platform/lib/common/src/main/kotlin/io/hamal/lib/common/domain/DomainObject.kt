package io.hamal.lib.common.domain

import io.hamal.lib.common.util.TimeUtils
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant


interface DomainObject<ID : DomainId> {
    val id: ID
    val partition get() = id.partition()
    val createdAt get() = CreatedAt(Instant.ofEpochMilli(id.value.elapsed().value + 1698451200000L))
    val updatedAt: UpdatedAt
}

@Serializable(with = CreatedAt.Serializer::class)
class CreatedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): CreatedAt = CreatedAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<CreatedAt>(::CreatedAt)
}

@Serializable(with = UpdatedAt.Serializer::class)
class UpdatedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): UpdatedAt = UpdatedAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<UpdatedAt>(::UpdatedAt)
}

abstract class DomainAt : ValueObject.ComparableImpl<Instant>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class DomainAtSerializer<AT : DomainAt>(
    val fn: (Instant) -> AT
) : KSerializer<AT> {
    override val descriptor = PrimitiveSerialDescriptor("At", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): AT {
        return fn(InstantSerializer.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: AT) {
        InstantSerializer.serialize(encoder, value.value)
    }
}


//@Serializer(forClass = Instant::class)
private object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeLong(value.toEpochMilli())
    override fun deserialize(decoder: Decoder) = Instant.ofEpochMilli(decoder.decodeLong())
} //End Circular Dependence Fix