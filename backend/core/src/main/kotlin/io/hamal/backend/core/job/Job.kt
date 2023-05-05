package io.hamal.backend.core.job

import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobId
import io.hamal.lib.vo.JobState

interface Job : DomainObject {

    val id: JobId
    val state: JobState
    val definitionId: JobDefinitionId

    class Planned(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.Planned
    }

    class Scheduled(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.Scheduled
    }

    class Started(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.Started
    }

    class Completed(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.Completed
    }

    class Failed(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.Failed
    }

    class TerminalFailed(
        override val id: JobId,
        override val definitionId: JobDefinitionId
    ) : Job {
        override val state = JobState.TerminalFailed
    }
}