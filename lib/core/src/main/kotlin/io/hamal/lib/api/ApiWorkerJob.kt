package io.hamal.lib.api

import io.hamal.lib.vo.JobId
import io.hamal.lib.vo.JobReference
import kotlinx.serialization.Serializable

@Serializable
data class ApiWorkerJob(
    val id: JobId,
    val reference: JobReference
)

@Serializable
data class ApiWorkerJobs(
    val jobs: List<ApiWorkerJob>
)