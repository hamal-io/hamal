package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.domain.vo.TaskId
import kotlinx.serialization.Serializable

@Serializable
sealed interface ApiWorkerTask {
    val taskId: TaskId
}

@Serializable
data class ApiWorkerScriptTask(
    override val taskId: TaskId,
    val code: Code
) : ApiWorkerTask

@Serializable
data class ApiWorkerJob(
    val id: JobId,
    val reference: JobReference,
    val tasks: List<ApiWorkerTask>
)

@Serializable
data class ApiWorkerJobs(
    val jobs: List<ApiWorkerJob>
)