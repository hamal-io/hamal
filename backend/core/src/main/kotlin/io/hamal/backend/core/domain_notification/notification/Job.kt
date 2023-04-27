package io.hamal.backend.core.domain_notification.notification

import io.hamal.backend.core.domain_notification.DomainNotificationTopic
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.RegionId
import kotlinx.serialization.Serializable


//sealed class JobDomainNotification(regionId: RegionId) : DomainNotification(regionId) {

@Serializable
@DomainNotificationTopic("scheduler::job_enqueued")
data class Scheduled(
    override val regionId: RegionId,
    val id: JobId,
    val inputs: Int
) : DomainNotification()

//}

