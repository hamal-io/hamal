package io.hamal.backend.core.model

import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.JobDefinitionId
import kotlinx.serialization.Serializable

@Serializable
data class JobDefinition(
    val id: JobDefinitionId,
    val triggers: List<Trigger>
) : DomainObject