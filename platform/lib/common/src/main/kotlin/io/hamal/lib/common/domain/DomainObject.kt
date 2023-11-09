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
    val createdAt get() = DomainCreatedAt(Instant.ofEpochMilli(id.value.elapsed().value))
    val updatedAt: DomainUpdatedAt
}


//Start Circular Dependence Fix
@Serializable(with = DomainCreatedAt.Serializer::class)
class DomainCreatedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): DomainCreatedAt = DomainCreatedAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<DomainCreatedAt>(::DomainCreatedAt)
}

@Serializable(with = DomainUpdatedAt.Serializer::class)
class DomainUpdatedAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): DomainUpdatedAt = DomainUpdatedAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<DomainUpdatedAt>(::DomainUpdatedAt)
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
}
//End Circular Dependence Fix