package io.hamal.backend.application.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.core.port.queue.EnqueueJobPort
import io.hamal.lib.ddd.usecase.ExecuteOneUseCase
import io.hamal.lib.ddd.usecase.ExecuteOneUseCaseOperation
import io.hamal.lib.vo.JobId
import io.hamal.lib.vo.RegionId

data class EnqueueJobUseCase(
    val jobId: JobId,
    val regionId: RegionId,
    val inputs: Int
) : ExecuteOneUseCase<QueuedJob.Enqueued> {
    class Operation(
        val enqueueJob: EnqueueJobPort,
        val notifyDomainPort: NotifyDomainPort
    ) : ExecuteOneUseCaseOperation<QueuedJob.Enqueued, EnqueueJobUseCase>(EnqueueJobUseCase::class) {
        override fun invoke(useCase: EnqueueJobUseCase): QueuedJob.Enqueued {
            println("Enqueue job")
            val result = enqueueJob(
                EnqueueJobPort.JobToEnqueue(
                    useCase.jobId, useCase.regionId
                )
            )
//            notifyDomainPort(JobToEnqueueued(result.id, result.regionId))
//            return listOf(result)
            TODO()
        }
    }
}