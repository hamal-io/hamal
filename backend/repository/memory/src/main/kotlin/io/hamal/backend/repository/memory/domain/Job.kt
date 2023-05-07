package io.hamal.backend.repository.memory.domain

import io.hamal.backend.core.job.PlannedJob
import io.hamal.backend.core.job.QueuedJob
import io.hamal.backend.core.job.ScheduledJob
import io.hamal.backend.repository.api.JobRepository
import io.hamal.lib.vo.QueuedAt
import io.hamal.lib.vo.ScheduledAt

object MemoryJobRepository : JobRepository {
    override fun planJob(jobToPlan: JobRepository.JobToPlan): PlannedJob {

        return PlannedJob(
            id = jobToPlan.id,
            definition = jobToPlan.definition,
            trigger = jobToPlan.trigger
        )
    }

    override fun schedule(planedJob: PlannedJob): ScheduledJob {
        return ScheduledJob(
            id = planedJob.id,
            definition = planedJob.definition,
            trigger = planedJob.trigger,
            scheduledAt = ScheduledAt.now()
        )
    }

    override fun queue(scheduledJob: ScheduledJob): QueuedJob {
        return QueuedJob(
            id = scheduledJob.id,
            definition = scheduledJob.definition,
            trigger = scheduledJob.trigger,
            queuedAt = QueuedAt.now()
        )
    }
}