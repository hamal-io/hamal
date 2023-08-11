package io.hamal.backend.instance.event

import io.hamal.backend.repository.api.*
import kotlinx.serialization.Serializable

@Serializable
@SystemTopic("exec::invoked")
data class ExecInvokedEvent(
    val scheduledExec: ScheduledExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::planned")
data class ExecPlannedEvent(
    val plannedExec: PlannedExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::scheduled")
data class ExecScheduledEvent(
    val scheduledExec: ScheduledExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::queued")
data class ExecutionQueuedEvent(
    val queuedExec: QueuedExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::started")
data class ExecutionStartedEvent(
    val startedExec: StartedExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::completed")
data class ExecutionCompletedEvent(
    val completedExec: CompletedExec
) : SystemEvent()

@Serializable
@SystemTopic("exec::failed")
data class ExecutionFailedEvent(
    val failedExec: FailedExec
) : SystemEvent()
