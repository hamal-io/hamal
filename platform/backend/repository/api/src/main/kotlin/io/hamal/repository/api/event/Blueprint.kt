package io.hamal.repository.api.event

import io.hamal.repository.api.Blueprint

data class BlueprintCreatedEvent(
    val blueprint: Blueprint
) : InternalEvent()


data class BlueprintUpdatedEvent(
    val blueprint: Blueprint,
) : InternalEvent()

