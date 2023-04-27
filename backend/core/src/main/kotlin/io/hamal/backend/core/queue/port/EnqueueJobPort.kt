package io.hamal.backend.core.queue.port

import io.hamal.backend.core.queue.model.QueuedJob
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.RegionId

interface EnqueueJobPort {
    data class JobToEnqueue(
        val jobId: JobId,
        val regionId: RegionId
    )

    operator fun invoke(jobToEnqueue: JobToEnqueue): QueuedJob.Enqueued
}