package io.hamal.backend.core.queue.port

import io.hamal.backend.core.queue.QueuedJob
import io.hamal.lib.core.vo.JobId
import io.hamal.lib.core.Shard

interface EnqueueJobPort {
    data class JobToEnqueue(
        val jobId: JobId,
        val shard: Shard
    )

    operator fun invoke(jobToEnqueue: JobToEnqueue): QueuedJob.Enqueued
}