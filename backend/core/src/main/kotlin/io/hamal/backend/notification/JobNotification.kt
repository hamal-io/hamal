package io.hamal.backend.notification

import io.hamal.lib.Shard
import io.hamal.lib.vo.JobId
import kotlinx.serialization.Serializable


@Serializable
@DomainNotificationTopic("job::scheduled")
data class JobScheduledNotification(
    override val shard: Shard,
    val id: JobId,
    val inputs: Int
) : DomainNotification()


