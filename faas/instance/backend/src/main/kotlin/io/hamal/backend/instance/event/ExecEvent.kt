package io.hamal.backend.instance.event

import io.hamal.repository.api.*
import kotlinx.serialization.Serializable

@Serializable
@SystemTopic("exec::invoked")
data class ExecInvokedEvent(
    val scheduledExec: io.hamal.repository.api.ScheduledExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::planned")
data class ExecPlannedEvent(
    val plannedExec: io.hamal.repository.api.PlannedExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::scheduled")
data class ExecScheduledEvent(
    val scheduledExec: io.hamal.repository.api.ScheduledExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::queued")
data class ExecutionQueuedEvent(
    val queuedExec: io.hamal.repository.api.QueuedExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::started")
data class ExecutionStartedEvent(
    val startedExec: io.hamal.repository.api.StartedExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::completed")
data class ExecutionCompletedEvent(
    val completedExec: io.hamal.repository.api.CompletedExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::failed")
data class ExecutionFailedEvent(
    val failedExec: io.hamal.repository.api.FailedExec
) : SystemEvent()
