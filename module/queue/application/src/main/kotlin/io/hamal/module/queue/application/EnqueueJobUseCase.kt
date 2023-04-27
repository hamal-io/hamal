package io.hamal.module.queue.application

import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.RegionId
import io.hamal.lib.domain_notification.NotifyDomainPort
import io.hamal.module.queue.core.job.EnqueueJobPort
import io.hamal.module.queue.core.job.EnqueueJobPort.JobToEnqueue
import io.hamal.lib.domain.QueuedJob

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
                JobToEnqueue(
                    useCase.jobId, useCase.regionId
                )
            )
//            notifyDomainPort(JobToEnqueueued(result.id, result.regionId))
//            return listOf(result)
            TODO()
        }
    }
}