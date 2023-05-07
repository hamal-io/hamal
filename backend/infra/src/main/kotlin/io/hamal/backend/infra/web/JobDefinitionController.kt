package io.hamal.backend.infra.web

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.tenant.Tenant
import io.hamal.backend.usecase.request.JobDefinitionRequest
import io.hamal.lib.core.RequestId
import io.hamal.lib.core.Shard
import io.hamal.lib.core.ddd.usecase.InvokeUseCasePort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
open class JobDefinitionController(
    @Autowired val request: InvokeUseCasePort,
) {
    @PostMapping("/v1/job-definitions")
    fun createJobDefinition(
        @RequestAttribute shard: Shard,
        @RequestAttribute requestId: RequestId,
        @RequestAttribute tenant: Tenant,
    ): JobDefinition {
        return request(
            JobDefinitionRequest.JobDefinitionCreation(
                requestId,
                shard
            )
        )
    }
}