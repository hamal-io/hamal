package io.hamal.lib.domain.vo

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