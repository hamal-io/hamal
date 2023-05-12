package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.JobReference
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