package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainIdSerializer
import io.hamal.lib.domain.vo.base.Reference
import io.hamal.lib.util.Snowflake
import kotlinx.serialization.Serializable

@Serializable(with = JobId.Serializer::class)
data class JobId(override val value: Snowflake.Id) : DomainId() {
    internal object Serializer : DomainIdSerializer<JobId>(::JobId)
}

@Serializable(with = JobDefinitionId.Serializer::class)
class JobDefinitionId(override val value: Snowflake.Id) : DomainId() {
    internal object Serializer : DomainIdSerializer<JobDefinitionId>(::JobDefinitionId)
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