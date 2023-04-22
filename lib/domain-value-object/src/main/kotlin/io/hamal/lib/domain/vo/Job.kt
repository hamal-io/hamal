package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.Reference
import kotlinx.serialization.Serializable

@Serializable
data class JobId(override val value: String) : Id(){init { validate() } }

@Serializable
class JobDefinitionId(override val value: String) : Id()

class JobReference(value: String) : Reference(value)

enum class JobState {
    Planned,
    Scheduled,
    Started,
    Completed,
    Failed,
    TerminalFailed
}