package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.value.TableEntry
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ExecId.Serializer::class)
class ExecId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<ExecId>(::ExecId)
}

@Serializable(with = ExecInputs .Serializer::class)
class ExecInputs(override val value: List<TableEntry>) : Inputs() {
    internal object Serializer : InputsSerializer<ExecInputs>(::ExecInputs)
}

enum class ExecState {
    Planned,
    Scheduled,
    Queued,
    Started,
    Completed,
    Failed,
    TerminalFailed
}

