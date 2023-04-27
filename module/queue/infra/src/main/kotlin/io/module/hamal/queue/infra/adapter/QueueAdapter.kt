package io.module.hamal.queue.infra.adapter

import io.hamal.lib.domain.vo.RegionId
import io.hamal.module.queue.core.job.DequeueJobPort
import io.hamal.module.queue.core.job.EnqueueJobPort
import io.hamal.module.queue.core.job.EnqueueJobPort.JobToEnqueue
import io.hamal.lib.domain.QueuedJob
import org.springframework.stereotype.Component

@Component
class QueueAdapter : EnqueueJobPort, DequeueJobPort {

    private val queue = mutableMapOf<RegionId, MutableList<QueuedJob.Enqueued>>()

    override fun invoke(regionId: RegionId): QueuedJob.Dequeued? {
        val enqueued = queue[regionId]?.removeFirst() ?: return null
        return QueuedJob.Dequeued(
            enqueued.id,
        )
    }

    override fun invoke(jobToEnqueue: JobToEnqueue): QueuedJob.Enqueued {
        queue.putIfAbsent(jobToEnqueue.regionId, mutableListOf())

        val result = QueuedJob.Enqueued(
            jobToEnqueue.jobId,
        )

        queue[jobToEnqueue.regionId]!!.plus(result)
        return result
    }
}