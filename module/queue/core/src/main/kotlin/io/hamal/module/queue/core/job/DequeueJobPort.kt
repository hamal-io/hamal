package io.hamal.module.queue.core.job

import io.hamal.lib.domain.vo.base.RegionId

interface DequeueJobPort {
    operator fun invoke(regionId: RegionId): Job.Dequeued?
}