package io.hamal.module.queue.core.job

import io.hamal.lib.domain.QueuedJob
import io.hamal.lib.domain.vo.RegionId

interface DequeueJobPort {
    operator fun invoke(regionId: RegionId): QueuedJob.Dequeued?
}