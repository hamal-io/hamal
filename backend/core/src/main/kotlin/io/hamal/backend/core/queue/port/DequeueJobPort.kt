package io.hamal.backend.core.queue.port

import io.hamal.backend.core.queue.QueuedJob
import io.hamal.lib.domain.Shard

interface DequeueJobPort {
    operator fun invoke(shard: Shard): QueuedJob.Dequeued?
}