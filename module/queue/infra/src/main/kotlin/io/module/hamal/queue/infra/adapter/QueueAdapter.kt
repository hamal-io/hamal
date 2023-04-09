package io.module.hamal.queue.infra.adapter

import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.module.queue.core.job.DequeueJobPort
import io.hamal.module.queue.core.job.EnqueueJobPort
import io.hamal.module.queue.core.job.EnqueueJobPort.JobToEnqueue
import io.hamal.module.queue.core.job.Job
import org.springframework.stereotype.Component

@Component
class QueueAdapter : EnqueueJobPort, DequeueJobPort {

    private val queue = mutableMapOf<RegionId, MutableList<Job.Enqueued>>()

    override fun invoke(regionId: RegionId): Job.Dequeued? {
        val enqueued = queue[regionId]?.removeFirst() ?: return null
        return Job.Dequeued(
            enqueued.id,
            enqueued.regionId
        )
    }

    override fun invoke(jobToEnqueue: JobToEnqueue): Job.Enqueued {
        queue.putIfAbsent(jobToEnqueue.regionId, mutableListOf())

        val result = Job.Enqueued(
            jobToEnqueue.jobId,
            jobToEnqueue.regionId
        )

        queue[jobToEnqueue.regionId]!!.plus(result)
        return result
    }
}