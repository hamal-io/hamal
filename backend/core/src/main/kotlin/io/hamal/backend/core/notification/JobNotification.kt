package io.hamal.backend.core.notification

import io.hamal.backend.core.job.PlannedJob
import io.hamal.backend.core.job.QueuedJob
import io.hamal.backend.core.job.ScheduledJob
import io.hamal.lib.Shard
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
