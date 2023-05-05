package io.hamal.backend.core.job_definition

import io.hamal.lib.Shard
import io.hamal.lib.ddd.base.DomainNotification
import io.hamal.lib.ddd.base.DomainNotificationTopic
import io.hamal.lib.vo.JobDefinitionId
import kotlinx.serialization.Serializable

@Serializable
@DomainNotificationTopic("job_definition::created")
data class JobDefinitionCreatedNotification(
    override val shard: Shard,
    val id: JobDefinitionId,
) : DomainNotification()
