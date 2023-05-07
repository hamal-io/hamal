package io.hamal.backend.infra.web

import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.core.RequestId
import io.hamal.lib.core.Shard
import io.hamal.lib.core.api.ApiWorkerJob
import io.hamal.lib.core.api.ApiWorkerJobs
import io.hamal.lib.core.ddd.usecase.InvokeRequestManyUseCasePort
import io.hamal.lib.core.vo.JobId
import io.hamal.lib.core.vo.JobReference
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