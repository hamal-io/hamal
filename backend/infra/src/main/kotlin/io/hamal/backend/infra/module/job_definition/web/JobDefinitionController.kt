package io.hamal.backend.infra.module.job_definition.web

import io.hamal.backend.application.job_definition.CreateJobDefinitionUseCase
import io.hamal.backend.core.model.JobDefinition
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.vo.RegionId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
open class JobDefinitionController(
    @Autowired val invokeUseCase: InvokeUseCasePort
) {

    @PostMapping("/v1/job-definitions")
    fun createJobDefinition(
        @RequestAttribute regionId: RegionId
    ): JobDefinition {
        return invokeUseCase(
            CreateJobDefinitionUseCase(regionId)
        )
    }
}