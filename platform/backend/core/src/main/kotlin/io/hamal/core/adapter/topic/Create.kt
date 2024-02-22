package io.hamal.core.adapter.topic

import io.hamal.core.adapter.namespace.NamespaceGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.request.TopicCreateRequest
import io.hamal.lib.domain.request.TopicCreateRequested
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TopicId
import org.springframework.stereotype.Component

fun interface TopicCreatePort {
    operator fun invoke(namespaceId: NamespaceId, req: TopicCreateRequest): TopicCreateRequested
}

@Component
class TopicCreateAdapter(
    private val namespaceGet: NamespaceGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : TopicCreatePort {
    override fun invoke(namespaceId: NamespaceId, req: TopicCreateRequest): TopicCreateRequested {
        if (req.type == TopicType.Internal) {
            throw IllegalArgumentException("Can not append internal topics")
        }
        val namespace = namespaceGet(namespaceId)
        return TopicCreateRequested(
            id = generateDomainId(::RequestId),
            by = SecurityContext.currentAuthId,
            status = RequestStatus.Submitted,
            topicId = generateDomainId(::TopicId),
            logTopicId = generateDomainId(::LogTopicId),
            workspaceId = namespace.workspaceId,
            namespaceId = namespace.id,
            name = req.name,
            type = req.type
        ).also(requestEnqueue::invoke)
    }

}