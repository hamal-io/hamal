package io.hamal.lib.domain

import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.lib.domain.vo.base.DomainAt
import io.hamal.lib.domain.vo.base.DomainAtSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

@Serializable
data class ExecLog(
    val id: ExecLogId,
    val execId: ExecId,
    val level: ExecLogLevel,
    val message: ExecLogMessage,
    val localAt: LocalAt,
    val remoteAt: RemoteAt
)

enum class ExecLogLevel {
    Trace,
    Debug,
    Info,
    Warn,
    Error,
    Fatal
}

@Serializable(with = ExecLogMessage.Serializer::class)
data class ExecLogMessage(val value: String) {
    object Serializer : KSerializer<ExecLogMessage> {
        override val descriptor = PrimitiveSerialDescriptor("LogMessage", STRING)
        override fun deserialize(decoder: Decoder) = ExecLogMessage(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: ExecLogMessage) {
            encoder.encodeString(value.value)
        }
    }
}

@Serializable(with = LocalAt.Serializer::class)
class LocalAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): LocalAt = LocalAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<LocalAt>(::LocalAt)
}

@Serializable(with = RemoteAt.Serializer::class)
class RemoteAt(override val value: Instant) : DomainAt() {
    companion object {
        @JvmStatic
        fun now(): RemoteAt = RemoteAt(TimeUtils.now())
    }

    internal object Serializer : DomainAtSerializer<RemoteAt>(::RemoteAt)
}