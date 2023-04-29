package io.hamal.backend.store.impl

import io.hamal.backend.core.model.JobDefinition
import io.hamal.backend.store.api.JobDefinitionStore
import io.hamal.lib.vo.JobDefinitionId

object DefaultJobDefinitionStore : JobDefinitionStore {

    val jobDefinitions = mutableMapOf<JobDefinitionId, JobDefinition>()
    override fun store(definition: JobDefinition) {
        jobDefinitions[definition.id] = definition
    }


}