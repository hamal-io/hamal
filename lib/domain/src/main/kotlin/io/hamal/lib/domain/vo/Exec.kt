package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.value.TableEntry
import io.hamal.lib.domain.vo.base.*
import kotlinx.serialization.Serializable

@Serializable(with = ExecId.Serializer::class)
class ExecId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<ExecId>(::ExecId)
}

@Serializable(with = ExecInputs.Serializer::class)
class ExecInputs(override val value: List<TableEntry>) : Inputs() {
    internal object Serializer : InputsSerializer<ExecInputs>(::ExecInputs)
}

@Serializable(with = ExecSecrets.Serializer::class)
class ExecSecrets(override val value: List<Secret>) : Secrets() {
    internal object Serializer : SecretsSerializer<ExecSecrets>(::ExecSecrets)
}


enum class ExecStatus(val value: Int) {

    Planned(1),
    Scheduled(2),
    Queued(3),
    Started(4),
    Completed(5),
    Failed(6),
    TerminalFailed(7);

    companion object {
        fun valueOf(value: Int) = requireNotNull(mapped[value]) { "$value is not an exec status" }

        private val mapped = ExecStatus.values()
            .associateBy { it.value }
    }
}

