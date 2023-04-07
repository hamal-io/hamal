package io.hamal.module.launchpad.infra.adapter

import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.module.launchpad.core.job.model.JobDefinition
import io.hamal.module.launchpad.core.job.port.CreateJobDefinitionPort
import io.hamal.module.launchpad.core.job.port.CreateJobDefinitionPort.JobDefinitionToCreate
import org.springframework.stereotype.Component
import java.util.*

@Component
class JobDefinitionAdapter : CreateJobDefinitionPort {
    override fun invoke(definitionToCreate: JobDefinitionToCreate): JobDefinition {
        return JobDefinition(
            id = JobDefinitionId(UUID.randomUUID().toString()),
            region = definitionToCreate.region
        )
    }
}