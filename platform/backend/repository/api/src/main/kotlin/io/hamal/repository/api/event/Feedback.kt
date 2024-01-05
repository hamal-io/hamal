package io.hamal.repository.api.event

import io.hamal.repository.api.Feedback

@PlatformEventTopic("feedback::created")
data class FeedbackCreatedEvent(
    val feedback: Feedback
) : PlatformEvent()

