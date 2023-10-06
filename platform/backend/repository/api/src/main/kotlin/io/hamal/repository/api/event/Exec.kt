package io.hamal.repository.api.event

import io.hamal.repository.api.*
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("exec::planned")
data class ExecPlannedEvent(
    val plannedExec: PlannedExec
) : PlatformEvent()

@Serializable
@PlatformEventTopic("exec::scheduled")
data class ExecScheduledEvent(
    val scheduledExec: ScheduledExec
) : PlatformEvent()

@Serializable
@PlatformEventTopic("exec::queued")
data class ExecutionQueuedEvent(
    val queuedExec: QueuedExec
) : PlatformEvent()

@Serializable
@PlatformEventTopic("exec::started")
data class ExecutionStartedEvent(
    val startedExec: StartedExec
) : PlatformEvent()

@Serializable
@PlatformEventTopic("exec::completed")
data class ExecutionCompletedEvent(
    val completedExec: CompletedExec
) : PlatformEvent()

@Serializable
@PlatformEventTopic("exec::failed")
data class ExecutionFailedEvent(
    val failedExec: FailedExec
) : PlatformEvent()
