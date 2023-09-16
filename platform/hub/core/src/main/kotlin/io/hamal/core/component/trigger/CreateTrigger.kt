package io.hamal.core.component.trigger

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.CreateTriggerReq
import org.springframework.stereotype.Component

@Component
class CreateTrigger(
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val submitRequest: SubmitRequest,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    operator fun <T : Any> invoke(
        req: CreateTriggerReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        ensureFuncExists(req)
        ensureTopicExists(req)
        ensureNamespaceExist(req)

        return responseHandler(submitRequest(req))
    }

    private fun ensureFuncExists(createTrigger: CreateTriggerReq) {
        funcQueryRepository.get(createTrigger.funcId)
    }

    private fun ensureTopicExists(createTrigger: CreateTriggerReq) {
        if (createTrigger.type == TriggerType.Event) {
            requireNotNull(createTrigger.topicId) { "topicId is missing" }
            eventBrokerRepository.getTopic(createTrigger.topicId!!)
        }
    }

    private fun ensureNamespaceExist(createTriggerReq: CreateTriggerReq) {
        createTriggerReq.namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}
