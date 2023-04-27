package io.hamal.module.launchpad.core.job.model

import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.JobState
import io.hamal.lib.domain.vo.JobState.*

sealed class Job(
    val id: JobId,
    val state: JobState,
    val definitionId: JobDefinitionId
) {

    class PlannedJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Planned,
        definitionId = definitionId
    )

    class ScheduledJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Scheduled,
        definitionId = definitionId
    )

    class StartedJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Started,
        definitionId = definitionId
    )

    class CompletedJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Completed,
        definitionId = definitionId
    )

    class FailedJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Failed,
        definitionId = definitionId
    )

    class TerminalFailedJob(
        id: JobId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = TerminalFailed,
        definitionId = definitionId
    )
}