package io.hamal.backend.infra.web

import io.hamal.backend.core.task.ScriptTask
import io.hamal.backend.usecase.request.JobRequest
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeRequestManyUseCasePort
import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.sdk.domain.ApiWorkerJob
import io.hamal.lib.sdk.domain.ApiWorkerJobs
import io.hamal.lib.sdk.domain.ApiWorkerScriptTask
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

        val result = requestMany.invoke(
            JobRequest.DequeueJob(
                requestId = RequestId(1111),
                shard = Shard(0)
            )
        )

        return ApiWorkerJobs(
            jobs = result.map {
                ApiWorkerJob(
                    id = it.id,
                    reference = JobReference("ref"),
                    tasks = it.definition.tasks.map { task ->
                        require(task is ScriptTask)
                        ApiWorkerScriptTask(task.id, task.code)
                    }
                )
            })
    }
}