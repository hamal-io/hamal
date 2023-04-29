package io.hamal.backend.infra

import io.hamal.backend.core.model.JobDefinition
import io.hamal.backend.core.model.Trigger
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.TriggerId

object DummyDb {
    val jobDefinitions = mutableMapOf<JobDefinitionId, JobDefinition>()
    val triggers = mutableMapOf<TriggerId, Trigger>()
}