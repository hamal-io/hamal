package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.TopicAppendEntryRequest
import io.hamal.lib.domain.request.TopicAppendEventRequested
import io.hamal.lib.domain.request.TopicCreateRequest
import io.hamal.lib.domain.request.TopicGroupCreateRequested
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.*
import io.hamal.repository.api.TopicQueryRepository.TopicEventQuery
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import org.springframework.stereotype.Component

interface TopicAppendEventPort {
    operator fun <T : Any> invoke(
        req: TopicAppendEntryRequest,
        responseHandler: (TopicAppendEventRequested) -> T
    ): T
}

interface TopicCreatePort {

    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: TopicCreateRequest,
        responseHandler: (TopicGroupCreateRequested) -> T
    ): T

}

interface TopicGetPort {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        responseHandler: (Topic) -> T
    ): T
}

interface TopicEventListPort {
    operator fun <T : Any> invoke(
        query: TopicEventQuery,
        responseHandler: (List<TopicEvent>, Topic) -> T
    ): T
}

interface TopicListPort {
    operator fun <T : Any> invoke(
        query: TopicQuery,
        responseHandler: (List<Topic>) -> T
    ): T
}

interface TopicPort : TopicAppendEventPort, TopicCreatePort, TopicGetPort, TopicListPort, TopicEventListPort

@Component
class TopicAdapter(
    private val topicRepository: TopicRepository,
    private val generateDomainId: GenerateId,
    private val groupRepository: GroupRepository,
    private val requestCmdRepository: RequestCmdRepository
) : TopicPort {

    override fun <T : Any> invoke(
        req: TopicAppendEntryRequest,
        responseHandler: (TopicAppendEventRequested) -> T
    ): T {
        topicRepository.get(req.topicId)
        return TopicAppendEventRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            topicId = req.topicId,
            payload = req.payload
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        groupId: GroupId,
        req: TopicCreateRequest,
        responseHandler: (TopicGroupCreateRequested) -> T
    ): T {
        groupRepository.get(groupId)
        return TopicGroupCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            topicId = generateDomainId(::TopicId),
            logTopicId = generateDomainId(::LogTopicId),
            groupId = groupId,
            name = req.name
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(topicId: TopicId, responseHandler: (Topic) -> T): T =
        responseHandler(topicRepository.get(topicId))

    override fun <T : Any> invoke(query: TopicQuery, responseHandler: (List<Topic>) -> T): T =
        responseHandler(topicRepository.list(query))

    override fun <T : Any> invoke(
        query: TopicEventQuery,
        responseHandler: (List<TopicEvent>, Topic) -> T
    ): T {
        val topic = topicRepository.get(query.topicId)
        return responseHandler(topicRepository.list(query), topic)
    }
}