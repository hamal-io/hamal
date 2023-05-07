package io.hamal.backend.infra.web

import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.api.ApiWorkerJob
import io.hamal.lib.ddd.usecase.InvokeRequestManyUseCasePort
import io.hamal.lib.vo.JobId
import io.hamal.lib.vo.JobReference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController

class QueueController
@Autowired constructor(
    val requestMany: InvokeRequestManyUseCasePort
) {
    @PostMapping("/v1/dequeue")
    fun dequeueJob(): List<ApiWorkerJob> {

        val result = requestMany.invoke(
            JobRequest.DequeueJob(
                requestId = RequestId(1111),
                shard = Shard(0)
            )
        )

        return result.map {
            ApiWorkerJob(
                id = JobId(1),
                reference = JobReference("ref")
            )
        }
    }
}