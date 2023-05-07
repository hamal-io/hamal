package io.hamal.lib.core.api

import io.hamal.lib.core.vo.JobId
import io.hamal.lib.core.vo.JobReference
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