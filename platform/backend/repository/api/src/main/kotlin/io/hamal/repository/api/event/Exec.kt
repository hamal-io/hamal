package io.hamal.repository.api.event

import io.hamal.repository.api.*

data class ExecPlannedEvent(
    val plannedExec: PlannedExec
) : PlatformEvent()

data class ExecScheduledEvent(
    val scheduledExec: ScheduledExec
) : PlatformEvent()

data class ExecQueuedEvent(
    val queuedExec: QueuedExec
) : PlatformEvent()

data class ExecStartedEvent(
    val startedExec: StartedExec
) : PlatformEvent()

data class ExecCompletedEvent(
    val completedExec: CompletedExec
) : PlatformEvent()

data class ExecFailedEvent(
    val failedExec: FailedExec
) : PlatformEvent()
