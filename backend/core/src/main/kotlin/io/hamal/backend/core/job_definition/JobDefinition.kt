package io.hamal.backend.core.job_definition

import io.hamal.backend.core.trigger.Trigger
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.JobReference
import kotlinx.serialization.Serializable

@Serializable
data class JobDefinition(
    override val id: JobDefinitionId,
    val reference: JobReference,
    val triggers: List<Trigger>
) : DomainObject<JobDefinitionId>