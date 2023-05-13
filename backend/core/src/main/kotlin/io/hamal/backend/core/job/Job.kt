package io.hamal.backend.core.job

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.trigger.Trigger
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.*
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
    override fun toString(): String {
        return "PlannedJob($id)"
    }

}

@Serializable
class ScheduledJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger,
    val scheduledAt: ScheduledAt
) : Job {
    override val state = JobState.Scheduled
    override fun toString(): String {
        return "ScheduledJob($id)"
    }

}

@Serializable
class QueuedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger,
    val queuedAt: QueuedAt
) : Job {
    override val state = JobState.Queued
    override fun toString(): String {
        return "QueuedJob($id)"
    }
}


@Serializable
class StartedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger
) : Job {
    override val state = JobState.Started
    override fun toString(): String {
        return "StartedJob($id)"
    }

}

@Serializable
class CompletedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger,
    val completedAt: CompletedAt
) : Job {
    override val state = JobState.Completed
    override fun toString(): String {
        return "CompletedJob($id)"
    }
}

@Serializable
class FailedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger
) : Job {
    override val state = JobState.Failed
    override fun toString(): String {
        return "FailedJob($id)"
    }

}

@Serializable
class TerminalFailedJob(
    override val id: JobId,
    override val definition: JobDefinition,
    override val trigger: Trigger
) : Job {
    override val state = JobState.TerminalFailed
    override fun toString(): String {
        return "TerminalFailedJob($id)"
    }
}
