package io.hamal.core.adapter.trigger

import io.hamal.core.adapter.endpoint.EndpointGetPort
import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.adapter.hook.HookGetPort
import io.hamal.core.adapter.namespace.NamespaceGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.adapter.topic.TopicGetPort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.request.TriggerCreateRequest
import io.hamal.lib.domain.request.TriggerCreateRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerInputs
import org.springframework.stereotype.Component

fun interface TriggerCreatePort {
    operator fun invoke(namespaceId: NamespaceId, req: TriggerCreateRequest): TriggerCreateRequested
}

@Component
class TriggerCreateAdapter(
    private val funcGet: FuncGetPort,
    private val namespaceGet: NamespaceGetPort,
    private val requestEnqueue: RequestEnqueuePort,
    private val generateDomainId: GenerateDomainId,
    private val topicGet: TopicGetPort,
    private val hookGet: HookGetPort,
    private val endpointGet: EndpointGetPort
) : TriggerCreatePort {
    override fun invoke(namespaceId: NamespaceId, req: TriggerCreateRequest): TriggerCreateRequested {
        val namespace = namespaceGet(namespaceId)
        val func = funcGet(req.funcId)

        ensureEvent(req)
        ensureHook(req)
        ensureEndpoint(req)
        return TriggerCreateRequested(
            type = req.type,
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = generateDomainId(::TriggerId),
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
            cron = req.cron,
            endpointId = req.endpointId
        ).also(requestEnqueue::invoke)
    }

    private fun ensureEvent(createTrigger: TriggerCreateRequest) {
        if (createTrigger.type == TriggerType.Event) {
            requireNotNull(createTrigger.topicId) { "topicId is missing" }
            topicGet(createTrigger.topicId!!)
        }
    }

    private fun ensureHook(createTrigger: TriggerCreateRequest) {
        if (createTrigger.type == TriggerType.Hook) {
            requireNotNull(createTrigger.hookId) { "hookId is missing" }
            requireNotNull(createTrigger.hookMethod) { "hookMethod is missing" }
            hookGet(createTrigger.hookId!!)
        }
    }

    private fun ensureEndpoint(createTrigger: TriggerCreateRequest) {
        if (createTrigger.type == TriggerType.Endpoint) {
            requireNotNull(createTrigger.endpointId) { "endpointId is missing" }
        }
    }
}

