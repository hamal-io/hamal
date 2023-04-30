package io.hamal.backend.usecase.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.backend.core.model.QueuedJob.Dequeued
import io.hamal.backend.core.port.queue.DequeueFlowPort
import io.hamal.lib.ddd.usecase.RequestId
import io.hamal.lib.ddd.usecase.RequestOneUseCase
import io.hamal.lib.ddd.usecase.RequestOneUseCaseHandler
import io.hamal.lib.vo.Shard

data class DequeueFlowUseCase(
    override val requestId: RequestId,
    val shard: Shard,
) : RequestOneUseCase<QueuedJob.Dequeued> {
    class Operation(
        val dequeueFlow: DequeueFlowPort
    ) : RequestOneUseCaseHandler<Dequeued, DequeueFlowUseCase>(DequeueFlowUseCase::class) {
        override fun invoke(useCase: DequeueFlowUseCase): Dequeued {
            TODO()
        }
    }
}