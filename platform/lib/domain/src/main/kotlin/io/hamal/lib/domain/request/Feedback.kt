package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.domain.vo.FeedbackMessage
import io.hamal.lib.domain.vo.RequestId

data class FeedbackCreateRequest(
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val accountId: AccountId?
)

data class FeedbackCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val feedbackId: FeedbackId,
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val accountId: AccountId?
) : Requested()