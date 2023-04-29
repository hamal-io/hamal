package io.hamal.backend.core.port.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.lib.vo.Shard

interface DequeueFlowPort {
    operator fun invoke(shard: Shard): QueuedJob.Dequeued?
}