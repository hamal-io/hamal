package io.hamal.backend.infra.notification

import io.hamal.backend.repository.api.domain.exec.*
import io.hamal.lib.domain.Shard
import kotlinx.serialization.Serializable


@Serializable
@DomainNotificationTopic("exec::planned")
data class ExecPlannedNotification(
    override val shard: Shard,
    val plannedExec: PlannedExec
) : DomainNotification()

@Serializable
@DomainNotificationTopic("exec::scheduled")
data class ExecScheduledNotification(
    override val shard: Shard,
    val scheduledExec: ScheduledExec
) : DomainNotification()

@Serializable
@DomainNotificationTopic("exec::queued")
data class ExecutionQueuedNotification(
    override val shard: Shard,
    val queuedExec: QueuedExec
) : DomainNotification()

@Serializable
@DomainNotificationTopic("exec::started")
data class ExecutionStartedNotification(
    override val shard: Shard,
    val startedExec: StartedExec
) : DomainNotification()

@Serializable
@DomainNotificationTopic("exec::completed")
data class ExecutionCompletedNotification(
    override val shard: Shard,
    val completeExec: CompleteExec
) : DomainNotification()

@Serializable
@DomainNotificationTopic("exec::failed")
data class ExecutionFailedNotification(
    override val shard: Shard,
    val failedExecution: FailedExecution
) : DomainNotification()
