package io.hamal.lib.domain_notification

import io.hamal.lib.ddd.base.DomainNotification
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.base.RegionId


sealed class JobDomainNotification : DomainNotification {

    data class Scheduled(
        val id: JobId,
        val regionId: RegionId,
        val inputs: Int
    ) : JobDomainNotification()

}