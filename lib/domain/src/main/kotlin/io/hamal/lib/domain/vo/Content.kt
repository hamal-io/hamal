package io.hamal.lib.domain.vo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

@Serializable(with = Content.Serializer::class)
data class Content(
    val value: ByteArray
) {
    constructor(value: String) : this(value.toByteArray(UTF_8))

    internal object Serializer : KSerializer<Content> {
        override val descriptor = PrimitiveSerialDescriptor("Content", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder) = Content(Base64.getDecoder().decode(decoder.decodeString()))

        override fun serialize(encoder: Encoder, value: Content) {
            encoder.encodeString(Base64.getEncoder().encodeToString(value.value))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Content
        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}

@Serializable(with = ContentType.Serializer::class)
data class ContentType(
    val value: String
) {
    internal object Serializer : KSerializer<ContentType> {
        override val descriptor = PrimitiveSerialDescriptor("ContentType", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder) = ContentType(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: ContentType) {
            encoder.encodeString(value.value)
        }
    }
}