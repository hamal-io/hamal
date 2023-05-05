package io.hamal.backend.infra.module.job_definition.web

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.tenant.Tenant
import io.hamal.backend.usecase.job_definition.JobDefinitionRequest.*
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.vo.port.GenerateDomainIdPort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
open class JobDefinitionController(
    @Autowired val request: InvokeUseCasePort,
    @Autowired val generateDomainId: GenerateDomainIdPort
) {

    @PostMapping("/v1/job-definitions")
    fun createJobDefinition(
        @RequestAttribute shard: Shard,
        @RequestAttribute tenant: Tenant,
    ): JobDefinition {
        return request(
            JobDefinitionCreation(
                RequestId(10),
                shard
            )
        )
    }
}