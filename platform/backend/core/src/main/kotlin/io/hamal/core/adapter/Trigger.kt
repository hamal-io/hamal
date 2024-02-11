package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.request.TriggerCreateRequest
import io.hamal.lib.domain.request.TriggerCreateRequested
import io.hamal.lib.domain.request.TriggerStatusRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import org.springframework.stereotype.Component


interface TriggerCreatePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: TriggerCreateRequest,
        responseHandler: (TriggerCreateRequested) -> T
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

interface TriggerStatusPort {
    operator fun <T : Any> invoke(
        triggerId: TriggerId,
        triggerStatus: TriggerStatus,
        responseHandler: (TriggerStatusRequested) -> T
    ): T
}

interface TriggerPort : TriggerCreatePort, TriggerGetPort, TriggerListPort, TriggerStatusPort

@Component
class TriggerAdapter(
    private val topicRepository: TopicRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateId,
    private val hookQueryRepository: HookQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val requestCmdRepository: RequestCmdRepository,
    private val triggerQueryRepository: TriggerQueryRepository,
) : TriggerPort {
    override fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: TriggerCreateRequest,
        responseHandler: (TriggerCreateRequested) -> T
    ): T {
        ensureFuncExists(req)
        ensureEvent(req)
        ensureHook(req)

        val namespace = namespaceQueryRepository.get(namespaceId)
        val func = funcQueryRepository.get(req.funcId)

        return TriggerCreateRequested(
            triggerType = req.type,
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            triggerId = generateDomainId(::TriggerId),
            groupId = namespace.groupId,
            name = req.name,
            funcId = func.id,
            namespaceId = namespaceId,
            inputs = req.inputs,
            correlationId = req.correlationId,
            duration = req.duration,
            topicId = req.topicId,
            hookId = req.hookId,
            hookMethod = req.hookMethod,
            cron = req.cron
        ).also(requestCmdRepository::queue).let(responseHandler)

    }

    override fun <T : Any> invoke(
        triggerId: TriggerId,
        responseHandler: (Trigger, Func, Namespace, Topic?, Hook?) -> T
    ): T {
        val trigger = triggerQueryRepository.get(triggerId)
        val func = funcQueryRepository.get(trigger.funcId)
        val namespace = namespaceQueryRepository.get(trigger.namespaceId)
        val topic = if (trigger is Trigger.Event) {
            topicRepository.get(trigger.topicId)
        } else {
            null
        }

        val hook = if (trigger is Trigger.Hook) {
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

        val topics =
            topicRepository.list(TopicQuery(topicIds = triggers.filterIsInstance<Trigger.Event>().map { it.topicId }))
                .associateBy { it.id }

        val hooks = hookQueryRepository.list(triggers.filterIsInstance<Trigger.Hook>().map { it.hookId })
            .associateBy { it.id }

        return responseHandler(triggers, funcs, namespaces, topics, hooks)
    }

    override fun <T : Any> invoke(
        triggerId: TriggerId,
        triggerStatus: TriggerStatus,
        responseHandler: (TriggerStatusRequested) -> T
    ): T {
        ensureTriggerExists(triggerId)
        return TriggerStatusRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            triggerId = triggerId,
            triggerStatus = triggerStatus
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    private fun ensureFuncExists(createTrigger: TriggerCreateRequest) {
        funcQueryRepository.get(createTrigger.funcId)
    }

    private fun ensureEvent(createTrigger: TriggerCreateRequest) {
        if (createTrigger.type == TriggerType.Event) {
            requireNotNull(createTrigger.topicId) { "topicId is missing" }
            topicRepository.get(createTrigger.topicId!!)
        }
    }

    private fun ensureHook(createTrigger: TriggerCreateRequest) {
        if (createTrigger.type == TriggerType.Hook) {
            requireNotNull(createTrigger.hookId) { "hookId is missing" }
            requireNotNull(createTrigger.hookMethod) { "hookMethod is missing" }
            hookQueryRepository.get(createTrigger.hookId!!)
        }
    }

    private fun ensureTriggerExists(triggerId: TriggerId) {
        triggerQueryRepository.get(triggerId)
    }
}

