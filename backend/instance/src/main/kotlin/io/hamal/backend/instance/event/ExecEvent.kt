package io.hamal.backend.instance.event

import io.hamal.lib.domain.*
import kotlinx.serialization.Serializable


@Serializable
@SystemEventTopic("exec::planned")
data class ExecPlannedEvent(
    val plannedExec: PlannedExec
) : SystemEvent()

@Serializable
@SystemEventTopic("exec::scheduled")
data class ExecScheduledEvent(
    val scheduledExec: ScheduledExec
) : SystemEvent()

@Serializable
@SystemEventTopic("exec::queued")
data class ExecutionQueuedEvent(
    val queuedExec: QueuedExec
) : SystemEvent()

@Serializable
@SystemEventTopic("exec::started")
data class ExecutionStartedEvent(
    val startedExec: StartedExec
) : SystemEvent()

@Serializable
@SystemEventTopic("exec::completed")
data class ExecutionCompletedEvent(
    val completedExec: CompletedExec
) : SystemEvent()

@Serializable
@SystemEventTopic("exec::failed")
data class ExecutionFailedEvent(
    val failedExec: FailedExec
) : SystemEvent()
