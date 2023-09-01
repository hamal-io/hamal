package io.hamal.backend.instance.event.event

import io.hamal.repository.api.*
import kotlinx.serialization.Serializable

@Serializable
@InstanceEventTopic("exec::planned")
data class ExecPlannedEvent(
    val plannedExec: PlannedExec
) : InstanceEvent()

@Serializable
@InstanceEventTopic("exec::scheduled")
data class ExecScheduledEvent(
    val scheduledExec: ScheduledExec
) : InstanceEvent()

@Serializable
@InstanceEventTopic("exec::queued")
data class ExecutionQueuedEvent(
    val queuedExec: QueuedExec
) : InstanceEvent()

@Serializable
@InstanceEventTopic("exec::started")
data class ExecutionStartedEvent(
    val startedExec: StartedExec
) : InstanceEvent()

@Serializable
@InstanceEventTopic("exec::completed")
data class ExecutionCompletedEvent(
    val completedExec: CompletedExec
) : InstanceEvent()

@Serializable
@InstanceEventTopic("exec::failed")
data class ExecutionFailedEvent(
    val failedExec: FailedExec
) : InstanceEvent()
