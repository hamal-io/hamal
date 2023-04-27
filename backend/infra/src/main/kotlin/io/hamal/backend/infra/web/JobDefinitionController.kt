package io.hamal.backend.infra.web

import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
open class JobDefinitionController(
    @Autowired val invokeUseCasePort: InvokeUseCasePort
) {

//    @PostMapping("/v1/job-definitions")
//    fun createJobDefinition(): JobDefinition? {
//        return invokeUseCasePort.command(
//            JobDefinition::class,
//            CreateJobDefinitionUseCase(
//                RegionId(0),
//                JobReference("some-ref")
//            )
//        ).firstOrNull()
//    }
}