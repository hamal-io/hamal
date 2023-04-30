package io.hamal.backend.core.port.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.lib.vo.FlowId
import io.hamal.lib.Shard

interface EnqueueFlowPort {
    data class FlowToEnqueue(
        val flowId: FlowId,
        val shard: Shard
    )

    operator fun invoke(flowToEnqueue: FlowToEnqueue): QueuedJob.Enqueued
}