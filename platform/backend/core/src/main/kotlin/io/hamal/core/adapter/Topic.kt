package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.request.TopicAppendEntryRequest
import io.hamal.lib.domain.request.TopicAppendEventRequested
import io.hamal.lib.domain.request.TopicCreateRequest
import io.hamal.lib.domain.request.TopicCreateRequested
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.*
import io.hamal.repository.api.TopicQueryRepository.TopicEventQuery
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import org.springframework.stereotype.Component

interface TopicAppendEventPort {
    operator fun invoke(req: TopicAppendEntryRequest): TopicAppendEventRequested
}

interface TopicCreatePort {
    operator fun invoke(namespaceId: NamespaceId, req: TopicCreateRequest): TopicCreateRequested
}

interface TopicGetPort {
    operator fun invoke(topicId: TopicId): Topic
}

interface TopicEventListPort {
    operator fun invoke(query: TopicEventQuery): List<TopicEvent>
}

interface TopicListPort {
    operator fun invoke(query: TopicQuery): List<Topic>
}

interface TopicPort : TopicAppendEventPort, TopicCreatePort, TopicGetPort, TopicListPort, TopicEventListPort

@Component
class TopicAdapter(
    private val topicRepository: TopicRepository,
    private val generateDomainId: GenerateId,
    private val namespaceRepository: NamespaceRepository,
    private val requestCmdRepository: RequestCmdRepository
) : TopicPort {

    override fun invoke(req: TopicAppendEntryRequest): TopicAppendEventRequested {
        topicRepository.get(req.topicId)
        return TopicAppendEventRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            topicId = req.topicId,
            payload = req.payload
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(namespaceId: NamespaceId, req: TopicCreateRequest): TopicCreateRequested {
        if (req.type == TopicType.Internal) {
            throw IllegalArgumentException("Can not append internal topics")
        }
        val namespace = namespaceRepository.get(namespaceId)
        return TopicCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            topicId = generateDomainId(::TopicId),
            logTopicId = generateDomainId(::LogTopicId),
            workspaceId = namespace.workspaceId,
            namespaceId = namespace.id,
            name = req.name,
            type = req.type
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(topicId: TopicId): Topic = topicRepository.get(topicId)

    override fun invoke(query: TopicQuery): (List<Topic>) = topicRepository.list(query)

    override fun invoke(query: TopicEventQuery): List<TopicEvent> = topicRepository.list(query)
}