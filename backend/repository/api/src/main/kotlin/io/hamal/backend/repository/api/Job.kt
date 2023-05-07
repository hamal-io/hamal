package io.hamal.backend.repository.api

import io.hamal.backend.core.job.PlannedJob
import io.hamal.backend.core.job.QueuedJob
import io.hamal.backend.core.job.ScheduledJob
import io.hamal.backend.core.job.StartedJob
import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.trigger.Trigger
import io.hamal.lib.core.RequestId
import io.hamal.lib.core.Shard
import io.hamal.lib.core.vo.JobId

interface JobRepository {
    fun planJob(jobToPlan: JobToPlan): PlannedJob
    fun schedule(planedJob: PlannedJob): ScheduledJob
    fun queue(scheduledJob: ScheduledJob): QueuedJob
    fun dequeue(): List<StartedJob>

    data class JobToPlan(
        val shard: Shard,
        val requestId: RequestId,
        val id: JobId,
        val definition: JobDefinition,
        val trigger: Trigger
    )

}