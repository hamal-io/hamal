package io.hamal.backend.core.job.model

import io.hamal.backend.core.DomainObject
import io.hamal.lib.domain.vo.JobDefinitionId

data class JobDefinition(
    val id: JobDefinitionId
) : DomainObject