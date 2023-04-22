package io.hamal.lib.domain_notification.notification

import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.lib.domain_notification.DomainNotificationTopic
import kotlinx.serialization.Serializable


//sealed class JobDomainNotification(regionId: RegionId) : DomainNotification(regionId) {

@Serializable
@DomainNotificationTopic("scheduler::job_enqueued")
class Scheduled(
    val id: JobId,
    override val regionId: RegionId,
    val inputs: Int
) : DomainNotification()

//}