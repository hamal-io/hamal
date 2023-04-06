package io.hamal.module.launchpad.infra.web

import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.domain.vo.Region
import io.hamal.module.launchpad.application.job.CreateJobDefinitionUseCase
import io.hamal.module.launchpad.core.job.model.JobDefinition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class JobDefinitionController(
    @Autowired val invokeUseCasePort: InvokeUseCasePort
) {

    @PostMapping("/v1/job-definitions")
    fun createJobDefinition(): JobDefinition? {
        return invokeUseCasePort.command(
            JobDefinition::class,
            CreateJobDefinitionUseCase(
                Region("this"),
                JobReference("some-ref")
            )
        ).firstOrNull()
    }
}