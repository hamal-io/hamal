package io.hamal.backend.application.queue

import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.core.model.QueuedJob
import io.hamal.backend.core.port.queue.EnqueueJobPort
import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.RegionId

data class EnqueueJobUseCase(
    val jobId: JobId,
    val regionId: RegionId,
    val inputs: Int
) : CommandUseCase {
    class Operation(
        val enqueueJob: EnqueueJobPort,
        val notifyDomainPort: NotifyDomainPort
    ) : CommandUseCaseOperation<QueuedJob.Enqueued, EnqueueJobUseCase>(
        QueuedJob.Enqueued::class, EnqueueJobUseCase::class
    ) {
        override fun invoke(useCase: EnqueueJobUseCase): List<QueuedJob.Enqueued> {
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