package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.request.TriggerCreateRequest
import io.hamal.lib.domain.request.TriggerCreateRequested
import io.hamal.lib.domain.request.TriggerStatusRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerInputs
import io.hamal.repository.api.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import org.springframework.stereotype.Component


interface TriggerCreatePort {
    operator fun invoke(namespaceId: NamespaceId, req: TriggerCreateRequest): TriggerCreateRequested
}

interface TriggerGetPort {
    operator fun invoke(triggerId: TriggerId): Trigger
}


interface TriggerListPort {
    operator fun invoke(query: TriggerQuery): List<Trigger>
}

interface TriggerSetStatusPort {
    operator fun invoke(triggerId: TriggerId, triggerStatus: TriggerStatus): TriggerStatusRequested
}

interface TriggerPort : TriggerCreatePort, TriggerGetPort, TriggerListPort, TriggerSetStatusPort

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
    override fun invoke(namespaceId: NamespaceId, req: TriggerCreateRequest): TriggerCreateRequested {
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
            workspaceId = namespace.workspaceId,
            name = req.name,
            funcId = func.id,
            namespaceId = namespaceId,
            inputs = req.inputs ?: TriggerInputs(),
            correlationId = req.correlationId,
            duration = req.duration,
            topicId = req.topicId,
            hookId = req.hookId,
            hookMethod = req.hookMethod,
            cron = req.cron
        ).also(requestCmdRepository::queue)

    }

    override fun invoke(triggerId: TriggerId): Trigger = triggerQueryRepository.get(triggerId)

    override operator fun invoke(query: TriggerQuery): List<Trigger> = triggerQueryRepository.list(query)


    override fun invoke(triggerId: TriggerId, triggerStatus: TriggerStatus): TriggerStatusRequested {
        ensureTriggerExists(triggerId)
        return TriggerStatusRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            triggerId = triggerId,
            triggerStatus = triggerStatus
        ).also(requestCmdRepository::queue)
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

