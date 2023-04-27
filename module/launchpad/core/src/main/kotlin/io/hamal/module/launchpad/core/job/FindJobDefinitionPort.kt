package io.hamal.module.launchpad.core.job

import io.hamal.lib.domain.JobDefinition
import io.hamal.lib.domain.vo.JobDefinitionId

interface FindJobDefinitionPort {
    fun findById(id: JobDefinitionId): JobDefinition
}