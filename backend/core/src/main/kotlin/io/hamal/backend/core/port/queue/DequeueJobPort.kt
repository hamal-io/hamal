package io.hamal.backend.core.port.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.lib.Shard

interface DequeueJobPort {
    operator fun invoke(shard: Shard): QueuedJob.Dequeued?
}