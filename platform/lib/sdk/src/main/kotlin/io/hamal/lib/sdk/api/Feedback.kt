package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.domain.vo.FeedbackMessage
import io.hamal.lib.domain.vo.RequestId

data class ApiFeedbackCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val feedbackId: FeedbackId
) : ApiRequested()

data class ApiFeedbackList(
    val feedbacks: List<Feedback>

) {
    data class Feedback(
        val id: FeedbackId,
        val mood: FeedbackMood
    )
}

data class ApiFeedback(
    val id: FeedbackId,
    val mood: FeedbackMood,
    val message: FeedbackMessage
) : ApiObject()
