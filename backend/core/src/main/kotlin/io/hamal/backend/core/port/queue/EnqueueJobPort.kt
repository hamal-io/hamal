package io.hamal.backend.core.port.queue

import io.hamal.backend.core.model.QueuedJob
import io.hamal.lib.vo.JobId
import io.hamal.lib.Shard

interface EnqueueJobPort {
    data class JobToEnqueue(
        val jobId: JobId,
        val shard: Shard
    )

    operator fun invoke(jobToEnqueue: JobToEnqueue): QueuedJob.Enqueued
}