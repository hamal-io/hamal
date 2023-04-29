package io.hamal.lib.vo

import io.hamal.lib.util.Snowflake
import io.hamal.lib.vo.base.DomainId
import io.hamal.lib.vo.base.DomainIdSerializer
import io.hamal.lib.vo.base.Reference
import io.hamal.lib.vo.base.ReferenceSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FlowId.Serializer::class)
class FlowId(override val value: Snowflake.Id) : DomainId() {
    internal object Serializer : DomainIdSerializer<FlowId>(::FlowId)
}

@Serializable(with = FlowDefinitionId.Serializer::class)
class FlowDefinitionId(override val value: Snowflake.Id) : DomainId() {
    internal object Serializer : DomainIdSerializer<FlowDefinitionId>(::FlowDefinitionId)
}


@Serializable(with = FlowReference.Serializer::class)
class FlowReference(override val value: Value) : Reference() {
    constructor(value: String) : this(Value(value))

    internal object Serializer : ReferenceSerializer<FlowReference>(::FlowReference)
}


enum class FlowState {
    Planned,
    Scheduled,
    Started,
    Completed,
    Failed,
    TerminalFailed
}