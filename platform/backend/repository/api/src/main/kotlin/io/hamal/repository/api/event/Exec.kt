package io.hamal.repository.api.event

import io.hamal.repository.api.*

@PlatformEventTopic("exec::planned")
data class ExecPlannedEvent(
    val plannedExec: PlannedExec
) : PlatformEvent()

@PlatformEventTopic("exec::scheduled")
data class ExecScheduledEvent(
    val scheduledExec: ScheduledExec
) : PlatformEvent()

@PlatformEventTopic("exec::queued")
data class ExecutionQueuedEvent(
    val queuedExec: QueuedExec
) : PlatformEvent()

@PlatformEventTopic("exec::started")
data class ExecutionStartedEvent(
    val startedExec: StartedExec
) : PlatformEvent()

@PlatformEventTopic("exec::completed")
data class ExecutionCompletedEvent(
    val completedExec: CompletedExec
) : PlatformEvent()

@PlatformEventTopic("exec::failed")
data class ExecutionFailedEvent(
    val failedExec: FailedExec
) : PlatformEvent()
