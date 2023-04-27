package io.hamal.backend.core.model

import io.hamal.backend.core.DomainObject
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobId
import io.hamal.lib.vo.JobState

sealed class Job(
    val id: JobId,
    val state: JobState,
    val definitionId: JobDefinitionId
) : DomainObject {

    class PlannedJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = JobState.Planned,
        definitionId = definitionId
    )

    class ScheduledJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = JobState.Scheduled,
        definitionId = definitionId
    )

    class StartedJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = JobState.Started,
        definitionId = definitionId
    )

    class CompletedJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = JobState.Completed,
        definitionId = definitionId
    )

    class FailedJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = JobState.Failed,
        definitionId = definitionId
    )

    class TerminalFailedJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = JobState.TerminalFailed,
        definitionId = definitionId
    )
}