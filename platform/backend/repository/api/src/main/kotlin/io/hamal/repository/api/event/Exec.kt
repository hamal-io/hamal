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
data class ExecQueuedEvent(
    val queuedExec: QueuedExec
) : PlatformEvent()

@PlatformEventTopic("exec::started")
data class ExecStartedEvent(
    val startedExec: StartedExec
) : PlatformEvent()

@PlatformEventTopic("exec::completed")
data class ExecCompletedEvent(
    val completedExec: CompletedExec
) : PlatformEvent()

@PlatformEventTopic("exec::failed")
data class ExecFailedEvent(
    val failedExec: FailedExec
) : PlatformEvent()
