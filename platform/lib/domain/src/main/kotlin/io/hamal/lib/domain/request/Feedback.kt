package io.hamal.lib.domain.request

import io.hamal.lib.domain.vo.*

data class FeedbackCreateRequest(
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val accountId: AccountId?
)

data class FeedbackCreateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: FeedbackId,
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val accountId: AccountId?
) : Requested()