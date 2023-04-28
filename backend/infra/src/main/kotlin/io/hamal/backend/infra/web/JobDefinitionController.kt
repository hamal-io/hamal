package io.hamal.backend.infra.web

import org.springframework.web.bind.annotation.RestController

@RestController
open class JobDefinitionController(
//    @Autowired val invokeUseCasePort: InvokeUseCasePort
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