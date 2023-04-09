package io.hamal.lib.domain_notification.notification

import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.lib.domain_notification.DomainNotification
import io.hamal.lib.domain_notification.DomainNotificationTopic


sealed class JobDomainNotification : DomainNotification {

    @DomainNotificationTopic("scheduler::job_enqueued")
    data class Scheduled(
        val id: JobId,
        override val regionId: RegionId,
        val inputs: Int
    ) : JobDomainNotification()

}