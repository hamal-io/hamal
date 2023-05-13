package io.hamal.backend.infra.web

import io.hamal.backend.usecase.query.JobQuery
import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeQueryOneUseCasePort
import io.hamal.lib.domain.ddd.InvokeRequestOneUseCasePort
import io.hamal.lib.domain.vo.JobId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class JobController(
    @Autowired val queryOne: InvokeQueryOneUseCasePort,
    @Autowired val request: InvokeRequestOneUseCasePort
) {

    @PostMapping("/v1/jobs/{jobId}/complete")
    fun completeJob(
        @PathVariable("jobId") stringJobId: String // FIXME be able to use value objects directly here
    ) {
        println("completing job $stringJobId")
        val jobId = JobId(SnowflakeId(stringJobId.toLong()))

        val startedJob = queryOne(JobQuery.GetStartedJob(jobId))
        request(
            JobRequest.CompleteStartedJob(
                requestId = RequestId(1234),
                shard = Shard(jobId.partition().value.toInt()), //FIXME
                startedJob = startedJob
            )
        )
    }

    @PostMapping("/v1/jobs/{jobId}/fail")
    fun failJob(
        @PathVariable("jobId") stringJobId: String
    ) {
        println("failing job $stringJobId")
    }


}