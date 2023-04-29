package io.hamal.backend.usecase.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.core.port.queue.EnqueueFlowPort
import io.hamal.lib.ddd.usecase.RequestOneUseCase
import io.hamal.lib.ddd.usecase.RequestOneUseCaseOperation
import io.hamal.lib.vo.FlowId
import io.hamal.lib.vo.Shard

data class EnqueueFlowUseCase(
    val flowId: FlowId,
    val shard: Shard,
    val inputs: Int
) : RequestOneUseCase<QueuedJob.Enqueued> {
    class Operation(
        val enqueueFlow: EnqueueFlowPort,
        val notifyDomainPort: NotifyDomainPort
    ) : RequestOneUseCaseOperation<QueuedJob.Enqueued, EnqueueFlowUseCase>(EnqueueFlowUseCase::class) {
        override fun invoke(useCase: EnqueueFlowUseCase): QueuedJob.Enqueued {
            println("Enqueue flow")
            val result = enqueueFlow(
                EnqueueFlowPort.FlowToEnqueue(
                    useCase.flowId, useCase.shard
                )
            )
//            notifyDomainPort(FlowToEnqueueued(result.id, result.shard))
//            return listOf(result)
            TODO()
        }
    }
}