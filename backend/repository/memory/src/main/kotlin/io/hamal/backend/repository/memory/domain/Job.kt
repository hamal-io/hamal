package io.hamal.backend.repository.memory.domain

import io.hamal.backend.core.job.*
import io.hamal.backend.repository.api.JobQueryRepository
import io.hamal.backend.repository.api.JobRequestRepository
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.vo.CompletedAt
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.QueuedAt
import io.hamal.lib.domain.vo.ScheduledAt

object MemoryJobRepository : JobRequestRepository, JobQueryRepository {

    private val queue = mutableListOf<QueuedJob>()
    private val startedJobs = mutableListOf<StartedJob>()

    override fun planJob(requestId: RequestId, jobToPlan: JobRequestRepository.JobToPlan): PlannedJob {

        return PlannedJob(
            id = jobToPlan.id,
            definition = jobToPlan.definition,
            trigger = jobToPlan.trigger
        )
    }

    override fun schedule(requestId: RequestId, planedJob: PlannedJob): ScheduledJob {
        return ScheduledJob(
            id = planedJob.id,
            definition = planedJob.definition,
            trigger = planedJob.trigger,
            scheduledAt = ScheduledAt.now()
        )
    }

    override fun queue(requestId: RequestId, scheduledJob: ScheduledJob): QueuedJob {
        val result = QueuedJob(
            id = scheduledJob.id,
            definition = scheduledJob.definition,
            trigger = scheduledJob.trigger,
            queuedAt = QueuedAt.now()
        )
        queue.add(result)
        return result
    }

    override fun complete(requestId: RequestId, startedJob: StartedJob): CompletedJob {
        startedJobs.removeIf { it.id == startedJob.id }
        return CompletedJob(
            id = startedJob.id,
            definition = startedJob.definition,
            trigger = startedJob.trigger,
            completedAt = CompletedAt.now()
        )
    }


    override fun dequeue(): List<StartedJob> {
        if (queue.isEmpty()) {
            return listOf()
        }

        val startedJob = queue.removeFirst().let {
            StartedJob(
                id = it.id,
                definition = it.definition,
                trigger = it.trigger,
            )
        }

        startedJobs.add(startedJob)

        return listOf(startedJob)
    }

    override fun findStartedJob(jobId: JobId): StartedJob? {
        return startedJobs.find { it.id == jobId }
    }
}