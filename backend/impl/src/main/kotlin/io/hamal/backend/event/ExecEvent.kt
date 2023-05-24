package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.exec.*
import io.hamal.lib.domain.Shard
import kotlinx.serialization.Serializable


@Serializable
@DomainNotificationTopic("exec::planned")
data class ExecPlannedNotification(
    override val shard: Shard,
    val plannedExec: PlannedExec
) : Event()

@Serializable
@DomainNotificationTopic("exec::scheduled")
data class ExecScheduledEvent(
    override val shard: Shard,
    val scheduledExec: ScheduledExec
) : Event()

@Serializable
@DomainNotificationTopic("exec::queued")
data class ExecutionQueuedEvent(
    override val shard: Shard,
    val queuedExec: QueuedExec
) : Event()

@Serializable
@DomainNotificationTopic("exec::started")
data class ExecutionStartedEvent(
    override val shard: Shard,
    val startedExec: StartedExec
) : Event()

@Serializable
@DomainNotificationTopic("exec::completed")
data class ExecutionCompletedEvent(
    override val shard: Shard,
    val completeExec: CompleteExec
) : Event()

@Serializable
@DomainNotificationTopic("exec::failed")
data class ExecutionFailedEvent(
    override val shard: Shard,
    val failedExecution: FailedExecution
) : Event()
