package io.hamal.backend.usecase.queue

import io.hamal.backend.core.queue.QueuedJob
import io.hamal.lib.ddd.port.NotifyDomainPort
import io.hamal.backend.core.queue.port.EnqueueJobPort
import io.hamal.lib.RequestId
import io.hamal.lib.ddd.usecase.RequestOneUseCase
import io.hamal.lib.ddd.usecase.RequestOneUseCaseHandler
import io.hamal.lib.vo.JobId
import io.hamal.lib.Shard

data class EnqueueJobUseCase(
    override val requestId: RequestId,
    override val shard: Shard,
    val jobId: JobId,
    val inputs: Int
) : RequestOneUseCase<QueuedJob.Enqueued> {
    class Operation(
        val enqueueJob: EnqueueJobPort,
        val notifyDomainPort: NotifyDomainPort
    ) : RequestOneUseCaseHandler<QueuedJob.Enqueued, EnqueueJobUseCase>(EnqueueJobUseCase::class) {
        override fun invoke(useCase: EnqueueJobUseCase): QueuedJob.Enqueued {
            println("Enqueue job")
            val result = enqueueJob(
                EnqueueJobPort.JobToEnqueue(
                    useCase.jobId, useCase.shard
                )
            )
//            notifyDomainPort(JobToEnqueueued(result.id, result.shard))
//            return listOf(result)
            TODO()
        }
    }
}