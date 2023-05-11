package io.hamal.backend.core.queue.port

import io.hamal.backend.core.queue.QueuedJob
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.Shard

interface EnqueueJobPort {
    data class JobToEnqueue(
        val jobId: JobId,
        val shard: Shard
    )

    operator fun invoke(jobToEnqueue: JobToEnqueue): QueuedJob.Enqueued
}