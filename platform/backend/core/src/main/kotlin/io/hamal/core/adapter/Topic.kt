package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.TopicAppendEntryRequest
import io.hamal.lib.domain.request.TopicAppendToRequested
import io.hamal.lib.domain.request.TopicCreateRequest
import io.hamal.lib.domain.request.TopicFlowCreateRequested
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.*
import io.hamal.repository.api.TopicQueryRepository.TopicEntryQuery
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import org.springframework.stereotype.Component

interface TopicAppendEntryPort {
    operator fun <T : Any> invoke(
        req: TopicAppendEntryRequest,
        responseHandler: (TopicAppendToRequested) -> T
    ): T
}

interface TopicCreatePort {

    operator fun <T : Any> invoke(
        flowId: FlowId,
        req: TopicCreateRequest,
        responseHandler: (TopicFlowCreateRequested) -> T
    ): T

}

interface TopicGetPort {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        responseHandler: (Topic) -> T
    ): T
}

interface TopicListEntryPort {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        query: TopicEntryQuery,
        responseHandler: (List<TopicEntry>, Topic) -> T
    ): T
}

interface TopicListPort {
    operator fun <T : Any> invoke(
        query: TopicQuery,
        responseHandler: (List<Topic>) -> T
    ): T
}

interface TopicPort : TopicAppendEntryPort, TopicCreatePort, TopicGetPort, TopicListPort, TopicListEntryPort

@Component
class TopicAdapter(
    private val topicRepository: TopicRepository,
    private val generateDomainId: GenerateId,
    private val flowQueryRepository: FlowQueryRepository,
    private val reqCmdRepository: RequestCmdRepository
) : TopicPort {

    override fun <T : Any> invoke(
        req: TopicAppendEntryRequest,
        responseHandler: (TopicAppendToRequested) -> T
    ): T {
        val topic = topicRepository.get(req.topicId)
        return TopicAppendToRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            topicId = req.topicId,
            payload = req.payload
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        flowId: FlowId,
        req: TopicCreateRequest,
        responseHandler: (TopicFlowCreateRequested) -> T
    ): T {
        val flow = flowQueryRepository.get(flowId)
        return TopicFlowCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            topicId = generateDomainId(::TopicId),
            logTopicId = generateDomainId(::LogTopicId),
            groupId = flow.groupId,
            flowId = flow.id,
            name = req.name
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(topicId: TopicId, responseHandler: (Topic) -> T): T =
        responseHandler(topicRepository.get(topicId))

    override fun <T : Any> invoke(query: TopicQuery, responseHandler: (List<Topic>) -> T): T =
        responseHandler(topicRepository.list(query))

    override fun <T : Any> invoke(
        topicId: TopicId,
        query: TopicEntryQuery,
        responseHandler: (List<TopicEntry>, Topic) -> T
    ): T {
        val topic = topicRepository.get(topicId)
        return responseHandler(topicRepository.list(query), topic)
    }
}