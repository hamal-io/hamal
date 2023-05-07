package io.hamal.backend.core.notification

import io.hamal.lib.core.Shard
import io.hamal.lib.core.vo.JobDefinitionId
import kotlinx.serialization.Serializable

@Serializable
@DomainNotificationTopic("job_definition::created")
data class JobDefinitionCreatedNotification(
    override val shard: Shard,
    val id: JobDefinitionId,
) : DomainNotification()
