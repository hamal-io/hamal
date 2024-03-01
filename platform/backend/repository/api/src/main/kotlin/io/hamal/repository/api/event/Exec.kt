package io.hamal.repository.api.event

import io.hamal.repository.api.Exec

data class ExecPlannedEvent(
    val plannedExec: Exec.Planned
) : InternalEvent()

data class ExecScheduledEvent(
    val scheduledExec: Exec.Scheduled
) : InternalEvent()

data class ExecQueuedEvent(
    val queuedExec: Exec.Queued
) : InternalEvent()

data class ExecStartedEvent(
    val startedExec: Exec.Started
) : InternalEvent()

data class ExecCompletedEvent(
    val completedExec: Exec.Completed
) : InternalEvent()

data class ExecFailedEvent(
    val failedExec: Exec.Failed
) : InternalEvent()
