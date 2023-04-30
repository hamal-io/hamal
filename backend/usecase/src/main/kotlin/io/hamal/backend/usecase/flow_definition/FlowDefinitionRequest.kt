package io.hamal.backend.usecase.flow_definition

import io.hamal.backend.core.model.FlowDefinition
import io.hamal.lib.ddd.usecase.RequestId
import io.hamal.lib.ddd.usecase.RequestOneUseCase
import io.hamal.lib.vo.Shard

sealed class FlowDefinitionRequest {
    data class FlowDefinitionCreation(
        override val requestId: RequestId,
        override val shard: Shard,
    ) : RequestOneUseCase<FlowDefinition>
}