package io.hamal.backend.usecase.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.backend.core.model.QueuedJob.Dequeued
import io.hamal.backend.core.port.queue.DequeueFlowPort
import io.hamal.lib.ddd.usecase.ExecuteOneUseCase
import io.hamal.lib.ddd.usecase.ExecuteOneUseCaseOperation
import io.hamal.lib.vo.Shard

data class DequeueFlowUseCase(
    val shard: Shard,
) : ExecuteOneUseCase<QueuedJob.Dequeued> {
    class Operation(
        val dequeueFlow: DequeueFlowPort
    ) : ExecuteOneUseCaseOperation<Dequeued, DequeueFlowUseCase>(DequeueFlowUseCase::class) {
        override fun invoke(useCase: DequeueFlowUseCase): Dequeued {
            TODO()
        }
    }
}