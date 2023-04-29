package io.hamal.backend.request.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.backend.core.model.QueuedJob.Dequeued
import io.hamal.backend.core.port.queue.DequeueFlowPort
import io.hamal.lib.ddd.usecase.ExecuteOneUseCase
import io.hamal.lib.ddd.usecase.ExecuteOneUseCaseOperation
import io.hamal.lib.vo.RegionId

data class DequeueFlowUseCase(
    val regionId: RegionId,
) : ExecuteOneUseCase<QueuedJob.Dequeued> {
    class Operation(
        val dequeueFlow: DequeueFlowPort
    ) : ExecuteOneUseCaseOperation<Dequeued, DequeueFlowUseCase>(DequeueFlowUseCase::class) {
        override fun invoke(useCase: DequeueFlowUseCase): Dequeued {
            TODO()
        }
    }
}