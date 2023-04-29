package io.hamal.backend.store.api

import io.hamal.backend.core.model.FlowDefinition

interface FlowDefinitionStore {
    fun create(definition: FlowDefinition)
}