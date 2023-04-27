package io.hamal.module.queue.application

import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.domain.vo.RegionId
import io.hamal.module.queue.core.job.DequeueJobPort
import io.hamal.lib.domain.QueuedJob

data class DequeueJobUseCase(
    val regionId: RegionId,
) : CommandUseCase {
    class Operation(
        val dequeueJob: DequeueJobPort
    ) : CommandUseCaseOperation<QueuedJob.Dequeued, DequeueJobUseCase>(
        QueuedJob.Dequeued::class, DequeueJobUseCase::class
    ) {
        override fun invoke(useCase: DequeueJobUseCase): List<QueuedJob.Dequeued> {
            return dequeueJob.invoke(useCase.regionId)?.let { listOf(it) } ?: listOf()
        }
    }
}