package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.Id
import io.hamal.lib.domain.vo.base.Reference

class JobId(value: String) : Id(value)

class JobDefinitionId(value: String) : Id(value)

class JobReference(value: String) : Reference(value)

enum class JobState {
    Planned,
    Scheduled,
    Started,
    Completed,
    Failed,
    TerminalFailed
}