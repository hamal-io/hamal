package io.hamal.backend.core.port.job

import io.hamal.backend.core.model.Job.CompletedJob
import io.hamal.lib.vo.JobId

fun interface CompleteJobPort {
    fun completeJob(jobId: JobId): CompletedJob
}