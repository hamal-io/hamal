package io.hamal.repository.api.event

import io.hamal.repository.api.*

data class ExecPlannedEvent(
    val plannedExec: PlannedExec
) : InternalEvent()

data class ExecScheduledEvent(
    val scheduledExec: ScheduledExec
) : InternalEvent()

data class ExecQueuedEvent(
    val queuedExec: QueuedExec
) : InternalEvent()

data class ExecStartedEvent(
    val startedExec: StartedExec
) : InternalEvent()

data class ExecCompletedEvent(
    val completedExec: CompletedExec
) : InternalEvent()

data class ExecFailedEvent(
    val failedExec: FailedExec
) : InternalEvent()
