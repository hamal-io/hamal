package io.hamal.module.launchpad.core.job.port

import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.domain.vo.Region
import io.hamal.module.launchpad.core.job.model.JobDefinition

interface CreateJobDefinitionPort {

    data class JobDefinitionToCreate(
        val reference: JobReference,
        val region: Region
    )

    fun createJobDefinition(definitionToCreate: JobDefinitionToCreate): JobDefinition

}