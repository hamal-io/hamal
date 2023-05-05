package io.hamal.backend.usecase.queue

import io.hamal.backend.core.queue.QueuedJob
import io.hamal.backend.core.queue.QueuedJob.Dequeued
import io.hamal.backend.core.queue.port.DequeueJobPort
import io.hamal.lib.RequestId
import io.hamal.lib.ddd.usecase.RequestOneUseCase
import io.hamal.lib.ddd.usecase.RequestOneUseCaseHandler
import io.hamal.lib.Shard

data class DequeueJobUseCase(
    override val requestId: RequestId,
    override val shard: Shard,
) : RequestOneUseCase<QueuedJob.Dequeued> {
    class Operation(
        val dequeueJob: DequeueJobPort
    ) : RequestOneUseCaseHandler<Dequeued, DequeueJobUseCase>(DequeueJobUseCase::class) {
        override fun invoke(useCase: DequeueJobUseCase): Dequeued {
            TODO()
        }
    }
}