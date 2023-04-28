package io.hamal.backend.core.model

import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.JobDefinitionId

data class JobDefinition(
    val id: JobDefinitionId
) : DomainObject