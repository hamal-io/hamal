package io.hamal.backend.application.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.backend.core.model.QueuedJob.Dequeued
import io.hamal.backend.core.port.queue.DequeueJobPort
import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.vo.RegionId

data class DequeueJobUseCase(
    val regionId: RegionId,
) : CommandUseCase<QueuedJob.Dequeued> {
    class Operation(
        val dequeueJob: DequeueJobPort
    ) : CommandUseCaseOperation<Dequeued, DequeueJobUseCase>(DequeueJobUseCase::class) {
        override fun invoke(useCase: DequeueJobUseCase): Dequeued {
            TODO()
        }
    }
}