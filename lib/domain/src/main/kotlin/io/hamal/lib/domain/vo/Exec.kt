package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ExecId.Serializer::class)
class ExecId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<ExecId>(::ExecId)
}

enum class ExecutionState {
    Planned,
    Scheduled,
    Queued,
    Started,
    Completed,
    Failed,
    TerminalFailed
}

