package io.hamal.backend.core.job.port

import io.hamal.backend.core.job.Job.CompletedJob
import io.hamal.lib.vo.JobId

fun interface CompleteJobPort {
    fun completeJob(jobId: JobId): CompletedJob
}