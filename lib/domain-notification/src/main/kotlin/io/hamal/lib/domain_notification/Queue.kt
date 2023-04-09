package io.hamal.lib.domain_notification

import io.hamal.lib.ddd.base.DomainNotification
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.base.RegionId

sealed class QueueDomainNotification : DomainNotification {

    data class JobEnqueued(
        val id: JobId,
        val regionId: RegionId
    ) : QueueDomainNotification()

}