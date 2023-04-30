package io.hamal.lib.vo

import io.hamal.lib.util.Snowflake
import io.hamal.lib.vo.base.DomainId
import io.hamal.lib.vo.base.DomainIdSerializer
import io.hamal.lib.vo.base.Reference
import io.hamal.lib.vo.base.ReferenceSerializer
import kotlinx.serialization.Serializable

@Serializable(with = JobId.Serializer::class)
class JobId(override val value: Snowflake.Id) : DomainId() {
    constructor(value: Int) : this(Snowflake.Id(value.toLong()))

    internal object Serializer : DomainIdSerializer<JobId>(::JobId)
}

@Serializable(with = JobDefinitionId.Serializer::class)
class JobDefinitionId(override val value: Snowflake.Id) : DomainId() {
    constructor(value: Int) : this(Snowflake.Id(value.toLong()))
    internal object Serializer : DomainIdSerializer<JobDefinitionId>(::JobDefinitionId)
}


@Serializable(with = JobReference.Serializer::class)
class JobReference(override val value: Value) : Reference() {
    constructor(value: String) : this(Value(value))

    internal object Serializer : ReferenceSerializer<JobReference>(::JobReference)
}


enum class JobState {
    Planned,
    Scheduled,
    Started,
    Completed,
    Failed,
    TerminalFailed
}