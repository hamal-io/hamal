package io.hamal.backend.usecase.job_definition

import io.hamal.backend.core.model.JobDefinition
import io.hamal.lib.RequestId
import io.hamal.lib.ddd.usecase.RequestOneUseCase
import io.hamal.lib.Shard

sealed class JobDefinitionRequest {
    data class JobDefinitionCreation(
        override val requestId: RequestId,
        override val shard: Shard,
    ) : RequestOneUseCase<JobDefinition>
}