package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.*
import io.hamal.lib.kua.type.StringType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.IllegalArgumentException

@Serializable(with = CodeId.Serializer::class)
class CodeId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : DomainIdSerializer<CodeId>(::CodeId)
}


@SerialName("CodeValue")
@Serializable(with = CodeValue.Serializer::class)
data class CodeValue(override val value: String) : ValueObject<String> {
    constructor(str: StringType) : this(str.value)

    object Serializer : KSerializer<CodeValue> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("CodeValue", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): CodeValue {
            return CodeValue(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: CodeValue) {
            encoder.encodeString(value.value)
        }
    }
}


@Serializable(with = CodeVersion.Serializer::class)
data class CodeVersion(override val value: Int) : IntValueObject() {
    internal object Serializer : IntValueObjectSerializer<CodeVersion>(::CodeVersion)
}


enum class CodeType(val value: Int) {
    None(0),
    Lua54(1);

    companion object {
        fun getByValue(value: Int): CodeType {
            val ct = values().find { it.value == value }
                ?: throw IllegalArgumentException("CodeType value: ${value} not existing")
            return ct
        }
    }
}

