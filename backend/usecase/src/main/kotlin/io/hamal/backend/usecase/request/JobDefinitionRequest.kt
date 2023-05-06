package io.hamal.backend.usecase.request

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.ddd.usecase.RequestOneUseCase

object JobDefinitionRequest {

    data class JobDefinitionCreation(
        override val requestId: RequestId,
        override val shard: Shard,

        ) : RequestOneUseCase<JobDefinition>

}