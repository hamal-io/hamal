package io.hamal.repository.api.event

import io.hamal.repository.api.*
import kotlinx.serialization.Serializable

@Serializable
@HubEventTopic("exec::planned")
data class ExecPlannedEvent(
    val plannedExec: PlannedExec
) : HubEvent()

@Serializable
@HubEventTopic("exec::scheduled")
data class ExecScheduledEvent(
    val scheduledExec: ScheduledExec
) : HubEvent()

@Serializable
@HubEventTopic("exec::queued")
data class ExecutionQueuedEvent(
    val queuedExec: QueuedExec
) : HubEvent()

@Serializable
@HubEventTopic("exec::started")
data class ExecutionStartedEvent(
    val startedExec: StartedExec
) : HubEvent()

@Serializable
@HubEventTopic("exec::completed")
data class ExecutionCompletedEvent(
    val completedExec: CompletedExec
) : HubEvent()

@Serializable
@HubEventTopic("exec::failed")
data class ExecutionFailedEvent(
    val failedExec: FailedExec
) : HubEvent()
