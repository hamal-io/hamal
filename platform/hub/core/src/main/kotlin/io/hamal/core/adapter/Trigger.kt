package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.Topic
import io.hamal.repository.api.submitted_req.SubmittedReqWithGroupId
import io.hamal.request.CreateTriggerReq
import org.springframework.stereotype.Component


interface CreateTriggerPort {
    operator fun <T : Any> invoke(
        req: CreateTriggerReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface GetTriggerPort {
    operator fun <T : Any> invoke(
        triggerId: TriggerId,
        responseHandler: (Trigger, Func, Namespace, Topic?) -> T
    ): T
}


interface ListTriggersPort {
    operator fun <T : Any> invoke(
        query: TriggerQuery,
        responseHandler: (
            triggers: List<Trigger>,
            funcs: Map<FuncId, Func>,
            namespaces: Map<NamespaceId, Namespace>,
            topics: Map<TopicId, Topic>
        ) -> T
    ): T
}

interface TriggerPort : CreateTriggerPort, GetTriggerPort, ListTriggersPort


@Component
class TriggerAdapter(
    private val submitRequest: SubmitRequest,
    private val triggerQueryRepository: TriggerQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository
) : TriggerPort {
    override fun <T : Any> invoke(req: CreateTriggerReq, responseHandler: (SubmittedReqWithGroupId) -> T): T {
        ensureFuncExists(req)
        ensureTopicExists(req)
        ensureNamespaceExist(req)

        return responseHandler(submitRequest(req))
    }

    override fun <T : Any> invoke(triggerId: TriggerId, responseHandler: (Trigger, Func, Namespace, Topic?) -> T): T {
        val trigger = triggerQueryRepository.get(triggerId)
        val func = funcQueryRepository.get(trigger.funcId)
        val namespace = namespaceQueryRepository.get(trigger.namespaceId)
        val topic = if (trigger is EventTrigger) {
            eventBrokerRepository.getTopic(trigger.topicId)
        } else {
            null
        }

        return responseHandler(
            trigger,
            func,
            namespace,
            topic
        )
    }

    override operator fun <T : Any> invoke(
        query: TriggerQuery,
        responseHandler: (
            triggers: List<Trigger>,
            funcs: Map<FuncId, Func>,
            namespaces: Map<NamespaceId, Namespace>,
            topics: Map<TopicId, Topic>
        ) -> T
    ): T {

        val triggers = triggerQueryRepository.list(query)

        val namespaces = namespaceQueryRepository.list(triggers.map { it.namespaceId })
            .associateBy { it.id }

        val funcs = funcQueryRepository.list(triggers.map { it.funcId })
            .associateBy { it.id }

        val topics = eventBrokerRepository.list(triggers.filterIsInstance<EventTrigger>().map { it.topicId })
            .associateBy { it.id }

        return responseHandler(triggers, funcs, namespaces, topics)
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
