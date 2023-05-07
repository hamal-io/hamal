package io.hamal.backend.infra.web

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class JobController {

    @PostMapping("/v1/jobs/{jobId}/complete")
    fun completeJob(
        @PathVariable("jobId") stringJobId: String
    ) {
        println("completing job $stringJobId")
    }

    @PostMapping("/v1/jobs/:jobId/fail")
    fun failJob() {
        println("failing job")
    }


}