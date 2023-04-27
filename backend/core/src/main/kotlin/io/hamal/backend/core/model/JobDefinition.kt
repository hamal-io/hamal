package io.hamal.backend.core.model

import io.hamal.backend.core.DomainObject
import io.hamal.lib.domain.vo.JobDefinitionId

data class JobDefinition(
    val id: JobDefinitionId
) : DomainObject