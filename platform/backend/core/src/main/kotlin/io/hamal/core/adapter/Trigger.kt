package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.Topic
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.CreateTriggerReq
import org.springframework.stereotype.Component


interface TriggerCreatePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: CreateTriggerReq,
        responseHandler: (SubmittedReq) -> T
    ): T
}

interface TriggerGetPort {
    operator fun <T : Any> invoke(
        triggerId: TriggerId,
        responseHandler: (Trigger, Func, Namespace, Topic?, Hook?) -> T
    ): T
}


interface TriggerListPort {
    operator fun <T : Any> invoke(
        query: TriggerQuery,
        responseHandler: (
            triggers: List<Trigger>,
            funcs: Map<FuncId, Func>,
            namespaces: Map<NamespaceId, Namespace>,
            topics: Map<TopicId, Topic>,
            hooks: Map<HookId, Hook>
        ) -> T
    ): T
}

interface TriggerPort : TriggerCreatePort, TriggerGetPort, TriggerListPort

@Component
class TriggerAdapter(
    private val submitRequest: SubmitRequest,
    private val triggerQueryRepository: TriggerQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val hookQueryRepository: HookQueryRepository
) : TriggerPort {
    override fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: CreateTriggerReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        ensureFuncExists(req)
        ensureTopicExists(req)
        ensureHookExists(req)

        return responseHandler(submitRequest(namespaceId, req))
    }

    override fun <T : Any> invoke(
        triggerId: TriggerId,
        responseHandler: (Trigger, Func, Namespace, Topic?, Hook?) -> T
    ): T {
        val trigger = triggerQueryRepository.get(triggerId)
        val func = funcQueryRepository.get(trigger.funcId)
        val namespace = namespaceQueryRepository.get(trigger.namespaceId)
        val topic = if (trigger is EventTrigger) {
            eventBrokerRepository.getTopic(trigger.topicId)
        } else {
            null
        }

        val hook = if (trigger is HookTrigger) {
            hookQueryRepository.get(trigger.hookId)
        } else {
            null
        }

        return responseHandler(trigger, func, namespace, topic, hook)
    }

    override operator fun <T : Any> invoke(
        query: TriggerQuery,
        responseHandler: (
            triggers: List<Trigger>,
            funcs: Map<FuncId, Func>,
            namespaces: Map<NamespaceId, Namespace>,
            topics: Map<TopicId, Topic>,
            hooks: Map<HookId, Hook>
        ) -> T
    ): T {

        val triggers = triggerQueryRepository.list(query)

        val namespaces = namespaceQueryRepository.list(triggers.map { it.namespaceId })
            .associateBy { it.id }

        val funcs = funcQueryRepository.list(triggers.map { it.funcId })
            .associateBy { it.id }

        val topics = eventBrokerRepository.list(triggers.filterIsInstance<EventTrigger>().map { it.topicId })
            .associateBy { it.id }

        val hooks = hookQueryRepository.list(triggers.filterIsInstance<HookTrigger>().map { it.hookId })
            .associateBy { it.id }

        return responseHandler(triggers, funcs, namespaces, topics, hooks)
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

    private fun ensureHookExists(createTrigger: CreateTriggerReq) {
        if (createTrigger.type == TriggerType.Hook) {
            requireNotNull(createTrigger.hookId) { "hookId is missing" }
            hookQueryRepository.get(createTrigger.hookId!!)
        }
    }
}
