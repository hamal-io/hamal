package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.TopicAppendEntryRequest
import io.hamal.lib.domain.request.TopicAppendToRequested
import io.hamal.lib.domain.request.TopicCreateRequest
import io.hamal.lib.domain.request.TopicFlowCreateRequested
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.FlowQueryRepository
import io.hamal.repository.api.RequestCmdRepository
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.BrokerRepository.TopicEntryQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.DepTopic
import io.hamal.repository.api.log.TopicEntry
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
        responseHandler: (DepTopic) -> T
    ): T
}

interface TopicListEntryPort {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        query: TopicEntryQuery,
        responseHandler: (List<TopicEntry>, DepTopic) -> T
    ): T
}

interface TopicListPort {
    operator fun <T : Any> invoke(
        query: TopicQuery,
        responseHandler: (List<DepTopic>) -> T
    ): T
}

interface TopicPort : TopicAppendEntryPort, TopicCreatePort, TopicGetPort, TopicListPort, TopicListEntryPort


@Component
class TopicAdapter(
    private val eventBrokerRepository: BrokerRepository,
    private val generateDomainId: GenerateId,
    private val flowQueryRepository: FlowQueryRepository,
    private val reqCmdRepository: RequestCmdRepository
) : TopicPort {

    override fun <T : Any> invoke(
        req: TopicAppendEntryRequest,
        responseHandler: (TopicAppendToRequested) -> T
    ): T {
        val topic = eventBrokerRepository.getTopic(req.topicId)
        return TopicAppendToRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            groupId = topic.groupId,
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
            groupId = flow.groupId,
            topicId = generateDomainId(::TopicId),
            flowId = flow.id,
            name = req.name
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(topicId: TopicId, responseHandler: (DepTopic) -> T): T =
        responseHandler(eventBrokerRepository.getTopic(topicId))

    override fun <T : Any> invoke(query: TopicQuery, responseHandler: (List<DepTopic>) -> T): T =
        responseHandler(eventBrokerRepository.listTopics(query))

    override fun <T : Any> invoke(
        topicId: TopicId,
        query: TopicEntryQuery,
        responseHandler: (List<TopicEntry>, DepTopic) -> T
    ): T {
        val topic = eventBrokerRepository.getTopic(topicId)
        return responseHandler(eventBrokerRepository.listEntries(topic, query), topic)
    }
}