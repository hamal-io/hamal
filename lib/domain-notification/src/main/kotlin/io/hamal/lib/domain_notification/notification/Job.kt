package io.hamal.lib.domain_notification.notification

import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.lib.domain_notification.DomainNotification
import io.hamal.lib.domain_notification.DomainNotificationTopic


sealed class JobDomainNotification(regionId: RegionId) : DomainNotification(regionId) {

    @DomainNotificationTopic("scheduler::job_enqueued")
    class Scheduled(
        val id: JobId,
        regionId: RegionId,
        val inputs: Int
    ) : JobDomainNotification(regionId)

}