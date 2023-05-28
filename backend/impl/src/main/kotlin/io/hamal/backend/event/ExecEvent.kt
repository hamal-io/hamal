package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.domain.Shard
import kotlinx.serialization.Serializable


@Serializable
@SystemEventTopic("exec::planned")
data class ExecPlannedEvent(
    override val shard: Shard,
    val plannedExec: PlannedExec
) : Event()

@Serializable
@SystemEventTopic("exec::scheduled")
data class ExecScheduledEvent(
    override val shard: Shard,
    val scheduledExec: ScheduledExec
) : Event()

@Serializable
@SystemEventTopic("exec::queued")
data class ExecutionQueuedEvent(
    override val shard: Shard,
    val queuedExec: QueuedExec
) : Event()

@Serializable
@SystemEventTopic("exec::started")
data class ExecutionStartedEvent(
    override val shard: Shard,
    val startedExec: StartedExec
) : Event()

@Serializable
@SystemEventTopic("exec::completed")
data class ExecutionCompletedEvent(
    override val shard: Shard,
    val completedExec: CompletedExec
) : Event()

@Serializable
@SystemEventTopic("exec::failed")
data class ExecutionFailedEvent(
    override val shard: Shard,
    val failedExec: FailedExec
) : Event()
