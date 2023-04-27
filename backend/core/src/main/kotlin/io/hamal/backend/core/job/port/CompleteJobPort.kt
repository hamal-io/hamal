package io.hamal.backend.core.job.port

import io.hamal.backend.core.job.model.Job.CompletedJob
import io.hamal.lib.domain.vo.JobId

fun interface CompleteJobPort {
    fun completeJob(jobId: JobId): CompletedJob
}