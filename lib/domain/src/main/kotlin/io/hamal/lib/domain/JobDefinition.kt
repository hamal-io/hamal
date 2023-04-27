package io.hamal.lib.domain

import io.hamal.lib.domain.vo.JobDefinitionId

data class JobDefinition(
    val id: JobDefinitionId
) : DomainObject