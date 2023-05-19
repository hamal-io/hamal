package io.hamal.backend.core.queue.port

import io.hamal.backend.core.queue.QueuedExec
import io.hamal.lib.domain.Shard

interface DequeueExecPort {
    operator fun invoke(shard: Shard): QueuedExec.Dequeued?
}