package io.hamal.request

import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.FeedbackMessage

interface FeedbackCreateReq {
    val mood: FeedbackMood
    val message: FeedbackMessage
    val accountId: AccountId?
}