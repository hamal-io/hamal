package io.hamal.backend.infra.web

import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.api.ApiWorkerJob
import io.hamal.lib.domain.api.ApiWorkerJobs
import io.hamal.lib.domain.ddd.usecase.InvokeRequestManyUseCasePort
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.JobReference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController

class QueueController
@Autowired constructor(
    val requestMany: InvokeRequestManyUseCasePort
) {
    @PostMapping("/v1/dequeue")
    fun dequeueJob(): ApiWorkerJobs {
//    fun dequeueJob(): ApiWorkerJob {

        val result = requestMany.invoke(
            JobRequest.DequeueJob(
                requestId = RequestId(1111),
                shard = Shard(0)
            )
        )

        return ApiWorkerJobs(
            jobs = result.map {
                ApiWorkerJob(
                    id = JobId(1),
                    reference = JobReference("ref")
                )
            })
    }
}