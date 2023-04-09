package io.hamal.module.queue.application

import io.hamal.lib.ddd.port.NotifyDomainPort
import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.lib.domain_notification.QueueDomainNotification
import io.hamal.lib.domain_notification.QueueDomainNotification.JobEnqueued
import io.hamal.module.queue.core.job.EnqueueJobPort
import io.hamal.module.queue.core.job.EnqueueJobPort.JobToEnqueue
import io.hamal.module.queue.core.job.Job

data class EnqueueJobUseCase(
    val jobId: JobId,
    val regionId: RegionId,
    val inputs: Int
) : CommandUseCase {
    class Operation(
        val enqueueJob: EnqueueJobPort,
        val notifyDomainPort: NotifyDomainPort<QueueDomainNotification>
    ) : CommandUseCaseOperation<Job.Enqueued, EnqueueJobUseCase>(
        Job.Enqueued::class, EnqueueJobUseCase::class
    ) {
        override fun invoke(useCase: EnqueueJobUseCase): List<Job.Enqueued> {
            println("Enqueue job")
            val result = enqueueJob(
                JobToEnqueue(
                    useCase.jobId, useCase.regionId
                )
            )
            notifyDomainPort(JobEnqueued(result.id, result.regionId))
            return listOf(result)
        }
    }
}