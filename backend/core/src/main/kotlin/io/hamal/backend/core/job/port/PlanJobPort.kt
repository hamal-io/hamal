package io.hamal.backend.core.job.port

import io.hamal.backend.core.job.Job.PlannedJob
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobId

fun interface PlanJobPort {
    fun planJob(jobToPlan: JobToPlan): PlannedJob
    data class JobToPlan(
        val jobId: JobId,
        val definitionId: JobDefinitionId
    )
}