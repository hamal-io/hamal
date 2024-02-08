package io.hamal.repository.api.event

import io.hamal.repository.api.Namespace

data class NamespaceCreatedEvent(
    val namespace: Namespace,
) : InternalEvent()


data class NamespaceUpdatedEvent(
    val namespace: Namespace,
) : InternalEvent()

