package io.hamal.repository.api.event

import io.hamal.repository.api.Namespace

data class NamespaceAppendedEvent(
    val namespace: Namespace,
) : InternalEvent()


data class NamespaceUpdatedEvent(
    val namespace: Namespace,
) : InternalEvent()

data class NamespaceDeletedEvent(
    val namespace: Namespace,
) : InternalEvent()
