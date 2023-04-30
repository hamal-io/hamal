package io.hamal.backend.core.model

import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobReference
import kotlinx.serialization.Serializable

@Serializable
data class JobDefinition(
    val id: JobDefinitionId,
    val reference: JobReference,
    val triggers: List<Trigger>
) : DomainObject