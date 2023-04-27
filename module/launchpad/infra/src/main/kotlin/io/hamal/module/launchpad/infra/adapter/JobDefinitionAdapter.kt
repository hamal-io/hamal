package io.hamal.module.launchpad.infra.adapter

import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort
import io.hamal.module.launchpad.core.job.model.JobDefinition
import io.hamal.module.launchpad.core.job.port.CreateJobDefinitionPort
import io.hamal.module.launchpad.core.job.port.CreateJobDefinitionPort.JobDefinitionToCreate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JobDefinitionAdapter(
    @Autowired val generateDomainId: GenerateDomainIdPort
) : CreateJobDefinitionPort {
    override fun invoke(definitionToCreate: JobDefinitionToCreate): JobDefinition {
        return JobDefinition(
            id = generateDomainId(definitionToCreate.regionId, ::JobDefinitionId),
            regionId = definitionToCreate.regionId
        )
    }
}