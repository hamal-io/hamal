package io.hamal.repository.api.event

import io.hamal.repository.api.Feedback

data class FeedbackCreatedEvent(
    val feedback: Feedback
) : InternalEvent()

