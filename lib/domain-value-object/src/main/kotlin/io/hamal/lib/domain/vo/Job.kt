package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.JobDefinitionId.JobDefinitionIdSerializer
import io.hamal.lib.domain.vo.JobId.JobIdSerializer
import io.hamal.lib.domain.vo.base.Id
import io.hamal.lib.domain.vo.base.IdSerializer
import io.hamal.lib.domain.vo.base.Reference
import kotlinx.serialization.Serializable

@Serializable(with = JobIdSerializer::class)
data class JobId(override val value: Value) : Id() {
    constructor(value: String) : this(Value(value))

    internal object JobIdSerializer : IdSerializer<JobId>(::JobId)
}


@Serializable(with = JobDefinitionIdSerializer::class)
class JobDefinitionId(override val value: Value) : Id() {
    constructor(value: String) : this(Value(value))

    internal object JobDefinitionIdSerializer : IdSerializer<JobDefinitionId>(::JobDefinitionId)
}


@Serializable
class JobReference(override val value: Value) : Reference() {
    constructor(value: String) : this(Value(value))
}


enum class JobState {
    Planned,
    Scheduled,
    Started,
    Completed,
    Failed,
    TerminalFailed
}