package io.hamal.backend.repository.api

import io.hamal.backend.core.job.*
import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.JobId

interface JobRequestRepository {

    fun planJob(requestId: RequestId, jobToPlan: JobToPlan): PlannedJob
    fun schedule(requestId: RequestId, planedJob: PlannedJob): ScheduledJob
    fun queue(requestId: RequestId, scheduledJob: ScheduledJob): QueuedJob
    fun complete(requestId: RequestId, startedJob: StartedJob): CompletedJob
    fun dequeue(): List<StartedJob>

    data class JobToPlan(
        val shard: Shard,
        val id: JobId,
        val definition: JobDefinition,
        val trigger: InvokedTrigger
    )

}

interface JobQueryRepository {
    fun findStartedJob(jobId: JobId): StartedJob?
}