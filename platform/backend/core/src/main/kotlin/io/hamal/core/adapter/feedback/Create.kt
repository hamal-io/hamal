package io.hamal.core.adapter.feedback

import io.hamal.core.adapter.RequestEnqueuePort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.FeedbackCreateRequest
import io.hamal.lib.domain.request.FeedbackCreateRequested
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface FeedbackCreatePort {
    operator fun invoke(req: FeedbackCreateRequest): FeedbackCreateRequested
}

@Component
class FeedbackCreateAdapter(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : FeedbackCreatePort {
    override fun invoke(req: FeedbackCreateRequest): FeedbackCreateRequested {
        return FeedbackCreateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            feedbackId = generateDomainId(::FeedbackId),
            mood = req.mood,
            message = req.message,
            accountId = req.accountId
        ).also(requestEnqueue::invoke)
    }
}