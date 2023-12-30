package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.FeedbackMessage
import io.hamal.lib.domain.vo.ReqId
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val accountId: AccountId?
) : Submitted