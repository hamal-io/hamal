package io.hamal.backend.core.notification

import io.hamal.lib.vo.JobId
import io.hamal.lib.vo.RegionId
import kotlinx.serialization.Serializable


@Serializable
@DomainNotificationTopic("scheduler::job_enqueued")
data class Scheduled(
    override val regionId: RegionId,
    val id: JobId,
    val inputs: Int
) : DomainNotification()


