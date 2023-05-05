package io.hamal.backend.core.job

import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobId
import io.hamal.lib.vo.JobState

interface Job : DomainObject {

    val id: JobId
    val state: JobState
    val definitionId: JobDefinitionId

    class PlannedJob(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.Planned
    }

    class ScheduledJob(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.Scheduled
    }

    class StartedJob(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.Started
    }

    class CompletedJob(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.Completed
    }

    class FailedJob(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.Failed
    }

    class TerminalFailedJob(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.TerminalFailed
    }
}