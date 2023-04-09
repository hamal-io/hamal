package io.hamal.lib.domain_notification

import io.hamal.lib.ddd.base.DomainNotification
import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.base.RegionId

sealed class JobDefinitionDomainNotification : DomainNotification {

    data class Created(
        val id: JobDefinitionId,
        val regionId: RegionId
    ) : JobDefinitionDomainNotification()

}