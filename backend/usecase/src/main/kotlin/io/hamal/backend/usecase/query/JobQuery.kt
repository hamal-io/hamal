package io.hamal.backend.usecase.query

import io.hamal.backend.core.job.StartedJob
import io.hamal.lib.domain.ddd.QueryOneUseCase
import io.hamal.lib.domain.vo.JobId

object JobQuery {

    data class GetStartedJob(
        val jobId: JobId
    ) : QueryOneUseCase<StartedJob>

}