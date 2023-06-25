package io.hamal.lib.domain

import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.domain.vo.EventId
import kotlinx.serialization.Serializable


@Serializable
data class Event(
    val contentType: ContentType,
    val content: Content
)

@Serializable
data class EventWithId(
    val id: EventId,
    val contentType: ContentType,
    val content: Content
)