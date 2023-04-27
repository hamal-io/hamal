package io.hamal.backend.core.queue.port

import io.hamal.backend.core.queue.model.QueuedJob
import io.hamal.lib.domain.vo.RegionId

interface DequeueJobPort {
    operator fun invoke(regionId: RegionId): QueuedJob.Dequeued?
}