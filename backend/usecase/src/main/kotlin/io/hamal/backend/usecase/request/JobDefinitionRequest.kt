package io.hamal.backend.usecase.request

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.lib.core.RequestId
import io.hamal.lib.core.Shard
import io.hamal.lib.core.ddd.usecase.RequestOneUseCase

object JobDefinitionRequest {

    data class JobDefinitionCreation(
        override val requestId: RequestId,
        override val shard: Shard,

        ) : RequestOneUseCase<JobDefinition>

}