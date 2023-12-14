package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.base.DomainAt
import io.hamal.lib.domain.vo.base.DomainAtSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant


@Serializable(with = ExecLogId.Serializer::class)
class ExecLogId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : SerializableDomainIdSerializer<ExecLogId>(::ExecLogId)
}

@Serializable(with = ExecLogMessage.Serializer::class)
data class ExecLogMessage(val value: String) {
    object Serializer : KSerializer<ExecLogMessage> {
        override val descriptor = PrimitiveSerialDescriptor("LogMessage", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder) = ExecLogMessage(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: ExecLogMessage) {
            encoder.encodeString(value.value)
        }
    }
}

@Serializable(with = ExecLogTimestamp.Serializer::class)
class ExecLogTimestamp(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): ExecLogTimestamp = ExecLogTimestamp(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<ExecLogTimestamp>(::ExecLogTimestamp)
}
