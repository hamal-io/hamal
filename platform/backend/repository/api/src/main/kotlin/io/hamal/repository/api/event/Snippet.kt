package io.hamal.repository.api.event

import io.hamal.repository.api.Snippet
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
@PlatformEventTopic("snippet::created")
data class SnippetCreatedEvent(
    val snippet: Snippet
) : PlatformEvent()


@Serializable
@PlatformEventTopic("snippet::updated")
data class SnippetUpdatedEvent(
    val snippet: Snippet,
) : PlatformEvent()

