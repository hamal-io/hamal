package io.hamal.backend.usecase.request

import io.hamal.backend.core.job.*
import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.RequestManyUseCase
import io.hamal.lib.domain.ddd.RequestOneUseCase

object JobRequest {
    data class PlanJob(
        override val requestId: RequestId,
        override val shard: Shard,
        val jobDefinition: JobDefinition,
        val trigger: InvokedTrigger
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

    data class CompleteStartedJob(
        override val requestId: RequestId,
        override val shard: Shard,
        val startedJob: StartedJob
    ) : RequestOneUseCase<CompletedJob>
}