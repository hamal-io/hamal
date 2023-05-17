package io.hamal.backend.usecase.request

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.RequestOneUseCase

object AdhocRequest {
    data class ExecuteJAdhocJob(
        override val requestId: RequestId,
        override val shard: Shard,
        val script: String
    ) : RequestOneUseCase<JobDefinition>

}