package io.hamal.core.adapter.trigger

import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.adapter.namespace.NamespaceGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.adapter.topic.TopicGetPort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain._enum.TriggerTypes
import io.hamal.lib.domain.request.TriggerCreateRequest
import io.hamal.lib.domain.request.TriggerCreateRequested
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
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
    private val topicGet: TopicGetPort
) : TriggerCreatePort {
    override fun invoke(namespaceId: NamespaceId, req: TriggerCreateRequest): TriggerCreateRequested {
        val namespace = namespaceGet(namespaceId)
        val func = funcGet(req.funcId)

        ensureEvent(req)
        return TriggerCreateRequested(
            type = req.type,
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            id = generateDomainId(::TriggerId),
            workspaceId = namespace.workspaceId,
            name = req.name,
            funcId = func.id,
            namespaceId = namespaceId,
            inputs = req.inputs ?: TriggerInputs(),
            correlationId = req.correlationId,
            duration = req.duration,
            topicId = req.topicId,
            cron = req.cron,
        ).also(requestEnqueue::invoke)
    }

    private fun ensureEvent(createTrigger: TriggerCreateRequest) {
        if (createTrigger.type == TriggerTypes.Event) {
            requireNotNull(createTrigger.topicId) { "topicId is missing" }
            topicGet(createTrigger.topicId!!)
        }
    }
}

