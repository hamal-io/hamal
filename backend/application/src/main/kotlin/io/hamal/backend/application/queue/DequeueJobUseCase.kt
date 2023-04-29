package io.hamal.backend.application.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.backend.core.model.QueuedJob.Dequeued
import io.hamal.backend.core.port.queue.DequeueJobPort
import io.hamal.lib.ddd.usecase.ExecuteOneUseCase
import io.hamal.lib.ddd.usecase.ExecuteOneUseCaseOperation
import io.hamal.lib.vo.RegionId

data class DequeueJobUseCase(
    val regionId: RegionId,
) : ExecuteOneUseCase<QueuedJob.Dequeued> {
    class Operation(
        val dequeueJob: DequeueJobPort
    ) : ExecuteOneUseCaseOperation<Dequeued, DequeueJobUseCase>(DequeueJobUseCase::class) {
        override fun invoke(useCase: DequeueJobUseCase): Dequeued {
            TODO()
        }
    }
}