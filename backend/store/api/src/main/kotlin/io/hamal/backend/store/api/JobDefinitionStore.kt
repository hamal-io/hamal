package io.hamal.backend.store.api

import io.hamal.backend.core.model.JobDefinition

interface JobDefinitionStore {
    fun store(definition: JobDefinition)
}