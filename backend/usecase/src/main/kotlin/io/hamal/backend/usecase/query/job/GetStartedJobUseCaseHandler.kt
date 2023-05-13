package io.hamal.backend.usecase.query.job

import io.hamal.backend.core.job.StartedJob
import io.hamal.backend.repository.api.JobQueryRepository
import io.hamal.backend.usecase.query.JobQuery.GetStartedJob
import io.hamal.lib.domain.ddd.QueryOneUseCaseHandler

class GetStartedJobUseCaseHandler(
    val jobQueryRepository: JobQueryRepository
) : QueryOneUseCaseHandler<StartedJob, GetStartedJob>(GetStartedJob::class) {
    override fun invoke(useCase: GetStartedJob): StartedJob {
        return jobQueryRepository.findStartedJob(useCase.jobId) ?: TODO() // FIXME
    }
}