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


enum class ExecStatus {

    Planned,
    Scheduled,
    Queued,
    InFlight,
    Completed,
    Failed,
    TerminalFailed
}

