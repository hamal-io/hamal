package io.hamal.lib.domain_notification.notification

import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.lib.domain_notification.DomainNotification
import io.hamal.lib.domain_notification.DomainNotificationTopic

sealed class JobDefinitionDomainNotification : DomainNotification {

    @DomainNotificationTopic("launchpad::job_enqueued")
    data class Created(
        val id: JobDefinitionId,
        override val regionId: RegionId,
    ) : JobDefinitionDomainNotification()

}