package io.hamal.backend.usecase.request

import io.hamal.backend.core.job.PlannedJob
import io.hamal.backend.core.job.QueuedJob
import io.hamal.backend.core.job.ScheduledJob
import io.hamal.backend.core.job.StartedJob
import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.trigger.Trigger
import io.hamal.lib.core.RequestId
import io.hamal.lib.core.Shard
import io.hamal.lib.core.ddd.usecase.RequestManyUseCase
import io.hamal.lib.core.ddd.usecase.RequestOneUseCase

object JobRequest {
    data class PlanJob(
        override val requestId: RequestId,
        override val shard: Shard,
        val jobDefinition: JobDefinition,
        val trigger: Trigger
    ) : RequestOneUseCase<PlannedJob>

    data class SchedulePlannedJob(
        override val requestId: RequestId,
        override val shard: Shard,
        val plannedJob: PlannedJob
    ) : RequestOneUseCase<ScheduledJob>

    data class QueueScheduledJob(
        override val requestId: RequestId,
        override val shard: Shard,
        val scheduledJob: ScheduledJob
    ) : RequestOneUseCase<QueuedJob>

    data class DequeueJob(
        override val requestId: RequestId,
        override val shard: Shard,
    ) : RequestManyUseCase<StartedJob>
}