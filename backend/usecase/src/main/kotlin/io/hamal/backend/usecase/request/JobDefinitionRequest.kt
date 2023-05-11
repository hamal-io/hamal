package io.hamal.backend.usecase.request

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.lib.domain.RequestId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.RequestOneUseCase

object JobDefinitionRequest {

    data class JobDefinitionCreation(
        override val requestId: RequestId,
        override val shard: Shard,

        ) : RequestOneUseCase<JobDefinition>

}