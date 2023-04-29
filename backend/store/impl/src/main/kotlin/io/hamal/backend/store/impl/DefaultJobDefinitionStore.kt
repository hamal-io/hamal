package io.hamal.backend.store.impl

import io.hamal.backend.core.model.FlowDefinition
import io.hamal.backend.store.api.FlowDefinitionStore
import io.hamal.lib.vo.FlowDefinitionId

object DefaultFlowDefinitionStore : FlowDefinitionStore {

    val flowDefinitions = mutableMapOf<FlowDefinitionId, FlowDefinition>()
    override fun create(definition: FlowDefinition) {
        flowDefinitions[definition.id] = definition
    }


}