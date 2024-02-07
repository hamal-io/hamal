package io.hamal.repository.api.event

import io.hamal.repository.api.Topic

data class TopicCreatedEvent(
    val topic: Topic
) : InternalEvent()