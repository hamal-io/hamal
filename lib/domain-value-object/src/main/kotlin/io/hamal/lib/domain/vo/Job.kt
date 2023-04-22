package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.Id
import io.hamal.lib.domain.vo.base.Reference
import kotlinx.serialization.Serializable

@Serializable
data class JobId(override val value: Value) : Id() {
    constructor(value: String) : this(Value(value))
}

@Serializable
class JobDefinitionId(override val value: Value) : Id() {
    constructor(value: String) : this(Value(value))
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