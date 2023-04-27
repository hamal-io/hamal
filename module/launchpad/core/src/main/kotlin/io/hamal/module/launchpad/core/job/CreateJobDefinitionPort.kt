package io.hamal.module.launchpad.core.job

import io.hamal.lib.domain.JobDefinition
import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.domain.vo.RegionId

interface CreateJobDefinitionPort {

    data class JobDefinitionToCreate(
        val reference: JobReference,
        val regionId: RegionId
    )

    operator fun invoke(definitionToCreate: JobDefinitionToCreate): JobDefinition

}