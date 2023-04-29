package io.hamal.backend.core.model

import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.FlowDefinitionId
import kotlinx.serialization.Serializable

@Serializable
data class FlowDefinition(
    val id: FlowDefinitionId,
    val triggers: List<Trigger>
) : DomainObject