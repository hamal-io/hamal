package io.hamal.backend.core.notification

import io.hamal.backend.core.job.*
import io.hamal.lib.domain.Shard
import kotlinx.serialization.Serializable


@Serializable
@DomainNotificationTopic("job::planned")
data class JobPlannedNotification(
    override val shard: Shard,
    val plannedJob: PlannedJob
) : DomainNotification()

@Serializable
@DomainNotificationTopic("job::scheduled")
data class JobScheduledNotification(
    override val shard: Shard,
    val scheduledJob: ScheduledJob
) : DomainNotification()

@Serializable
@DomainNotificationTopic("job::queued")
data class JobQueuedNotification(
    override val shard: Shard,
    val queuedJob: QueuedJob
) : DomainNotification()

@Serializable
@DomainNotificationTopic("job::started")
data class JobStartedNotification(
    override val shard: Shard,
    val startedJob: StartedJob
) : DomainNotification()

@Serializable
@DomainNotificationTopic("job::completed")
data class JobCompletedNotification(
    override val shard: Shard,
    val completedJob: CompletedJob
) : DomainNotification()

@Serializable
@DomainNotificationTopic("job::failed")
data class JobFailedNotification(
    override val shard: Shard,
    val failedJob: FailedJob
) : DomainNotification()
