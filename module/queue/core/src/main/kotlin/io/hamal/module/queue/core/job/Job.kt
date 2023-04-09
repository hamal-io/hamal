package io.hamal.module.queue.core.job

import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.base.RegionId
import java.util.*

sealed class Job(
    val id: JobId,
    val regionId: RegionId
) {
    class Enqueued(
        id: JobId,
        regionId: RegionId
    ) : Job(id, regionId) {
        companion object {
            fun template(): Enqueued = Enqueued(
                JobId(UUID.randomUUID().toString()),
                RegionId("some-region")
            )
        }
    }


    class Dequeued(
        id: JobId, regionId: RegionId
    ) : Job(id, regionId)
}