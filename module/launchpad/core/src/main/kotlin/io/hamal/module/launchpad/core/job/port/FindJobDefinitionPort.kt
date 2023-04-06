package io.hamal.module.launchpad.core.job.port

import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.module.launchpad.core.job.model.JobDefinition

interface FindJobDefinitionPort {
    fun findById(id: JobDefinitionId): JobDefinition
}