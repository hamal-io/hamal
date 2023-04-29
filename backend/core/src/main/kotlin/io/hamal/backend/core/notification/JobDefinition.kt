package io.hamal.backend.core.notification

import io.hamal.backend.core.model.JobDefinition
import io.hamal.lib.vo.RegionId
import kotlinx.serialization.Serializable


sealed class JobDefinitionDomainNotification : DomainNotification() {
    @Serializable
    @DomainNotificationTopic("job_definition_created")
    data class Created(
        val jobDefinition: JobDefinition,
        override val regionId: RegionId
    ) : JobDefinitionDomainNotification()
}