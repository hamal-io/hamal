package io.hamal.backend.core.port.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.lib.vo.RegionId

interface DequeueJobPort {
    operator fun invoke(regionId: RegionId): QueuedJob.Dequeued?
}