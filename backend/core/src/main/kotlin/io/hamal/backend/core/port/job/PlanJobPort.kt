package io.hamal.backend.core.port.job

import io.hamal.backend.core.model.Job.PlannedJob
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobId

fun interface PlanJobPort {
    fun planJob(jobToPlan: JobToPlan): PlannedJob
    data class JobToPlan(
        val jobId: JobId,
        val definitionId: JobDefinitionId
    )
}