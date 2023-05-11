package io.hamal.backend.core.job

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.trigger.Trigger
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.JobState
import io.hamal.lib.domain.vo.QueuedAt
import io.hamal.lib.domain.vo.ScheduledAt
import kotlinx.serialization.Serializable

@Serializable
sealed interface Job : DomainObject<JobId> {

    override val id: JobId
    val state: JobState
    val definition: JobDefinition
    val trigger: Trigger
}

@Serializable
class PlannedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger
) : Job {
    override val state = JobState.Planned
}

@Serializable
class ScheduledJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger,
    val scheduledAt: ScheduledAt
) : Job {
    override val state = JobState.Scheduled
}

@Serializable
class QueuedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger,
    val queuedAt: QueuedAt
) : Job {
    override val state = JobState.Queued
}


@Serializable
class StartedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger
) : Job {
    override val state = JobState.Started
}

@Serializable
class CompletedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger
) : Job {
    override val state = JobState.Completed
}

@Serializable
class FailedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger
) : Job {
    override val state = JobState.Failed
}

@Serializable
class TerminalFailedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger
) : Job {
    override val state = JobState.TerminalFailed
}
