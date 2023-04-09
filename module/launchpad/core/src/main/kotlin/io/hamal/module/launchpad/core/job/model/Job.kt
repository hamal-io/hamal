package io.hamal.module.launchpad.core.job.model

import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.JobState
import io.hamal.lib.domain.vo.JobState.*
import io.hamal.lib.domain.vo.base.RegionId

sealed class Job(
    val id: JobId,
    val state: JobState,
    val regionId: RegionId,
    val definitionId: JobDefinitionId
) {

    class PlannedJob(
        id: JobId,
        regionId: RegionId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Planned,
        regionId = regionId,
        definitionId = definitionId
    )

    class ScheduledJob(
        id: JobId,
        regionId: RegionId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Scheduled,
        regionId = regionId,
        definitionId = definitionId
    )

    class StartedJob(
        id: JobId,
        regionId: RegionId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Started,
        regionId = regionId,
        definitionId = definitionId
    )

    class CompletedJob(
        id: JobId,
        regionId: RegionId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Completed,
        regionId = regionId,
        definitionId = definitionId
    )

    class FailedJob(
        id: JobId,
        regionId: RegionId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = Failed,
        regionId = regionId,
        definitionId = definitionId
    )

    class TerminalFailedJob(
        id: JobId,
        regionId: RegionId,
        definitionId: JobDefinitionId
    ) : Job(
        id = id,
        state = TerminalFailed,
        regionId = regionId,
        definitionId = definitionId
    )
}