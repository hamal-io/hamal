package io.hamal.lib.domain_notification.notification

import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain_notification.DomainNotificationTopic

//sealed class QueueDomainNotification(regionId: RegionId) : DomainNotification(regionId) {


    @DomainNotificationTopic("queue:job_enqueued")
    class JobEnqueued(
        val id: JobId,
        regionId: String,
    ) : DomainNotification(regionId)

//}