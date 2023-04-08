package io.hamal.module.launchpad.core.job.model

import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.JobState
import io.hamal.lib.domain.vo.JobState.*
import io.hamal.lib.domain.vo.base.Region

sealed class Job(
    val id: JobId,
    val state: JobState,
    val region: Region,
    val definitionId: JobDefinitionId
) {

    class PlannedJob(
        id: JobId,
        region: Region,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Planned,
        region = region,
        definitionId = definitionId
    )

    class ScheduledJob(
        id: JobId,
        region: Region,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Scheduled,
        region = region,
        definitionId = definitionId
    )

    class StartedJob(
        id: JobId,
        region: Region,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Started,
        region = region,
        definitionId = definitionId
    )

    class CompletedJob(
        id: JobId,
        region: Region,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Completed,
        region = region,
        definitionId = definitionId
    )

    class FailedJob(
        id: JobId,
        region: Region,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Failed,
        region = region,
        definitionId = definitionId
    )

    class TerminalFailedJob(
        id: JobId,
        region: Region,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = TerminalFailed,
        region = region,
        definitionId = definitionId
    )
}