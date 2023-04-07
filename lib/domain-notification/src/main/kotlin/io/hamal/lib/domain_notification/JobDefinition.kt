package io.hamal.lib.domain_notification

import io.hamal.lib.ddd.base.DomainNotification
import io.hamal.lib.domain.vo.JobDefinitionId

sealed class JobDefinitionDomainNotification : DomainNotification {

    data class Created(
        val id: JobDefinitionId
    ) : JobDefinitionDomainNotification()

}