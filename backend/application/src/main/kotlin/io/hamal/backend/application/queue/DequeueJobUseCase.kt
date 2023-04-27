package io.hamal.backend.application.queue

import io.hamal.backend.core.model.QueuedJob.Dequeued
import io.hamal.backend.core.port.queue.DequeueJobPort
import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.domain.vo.RegionId

data class DequeueJobUseCase(
    val regionId: RegionId,
) : CommandUseCase {
    class Operation(
        val dequeueJob: DequeueJobPort
    ) : CommandUseCaseOperation<Dequeued, DequeueJobUseCase>(
        Dequeued::class, DequeueJobUseCase::class
    ) {
        override fun invoke(useCase: DequeueJobUseCase): List<Dequeued> {
            return dequeueJob.invoke(useCase.regionId)?.let { listOf(it) } ?: listOf()
        }
    }
}