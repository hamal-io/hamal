package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.common.Shard
import kotlinx.serialization.Serializable


@Serializable
@EventTopic("exec::planned")
data class ExecPlannedEvent(
    override val shard: Shard,
    val plannedExec: PlannedExec
) : Event()

@Serializable
@EventTopic("exec::scheduled")
data class ExecScheduledEvent(
    override val shard: Shard,
    val scheduledExec: ScheduledExec
) : Event()

@Serializable
@EventTopic("exec::queued")
data class ExecutionQueuedEvent(
    override val shard: Shard,
    val queuedExec: QueuedExec
) : Event()

@Serializable
@EventTopic("exec::started")
data class ExecutionStartedEvent(
    override val shard: Shard,
    val startedExec: StartedExec
) : Event()

@Serializable
@EventTopic("exec::completed")
data class ExecutionCompletedEvent(
    override val shard: Shard,
    val completedExec: CompletedExec
) : Event()

@Serializable
@EventTopic("exec::failed")
data class ExecutionFailedEvent(
    override val shard: Shard,
    val failedExec: FailedExec
) : Event()
