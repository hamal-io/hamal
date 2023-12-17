package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.FlowQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.BrokerRepository.TopicEntryQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.Topic
import io.hamal.repository.api.log.TopicEntry
import io.hamal.repository.api.submitted_req.TopicAppendToSubmitted
import io.hamal.repository.api.submitted_req.TopicCreateSubmitted
import io.hamal.request.TopicAppendEntryReq
import io.hamal.request.TopicCreateReq
import org.springframework.stereotype.Component

interface TopicAppendEntryPort {
    operator fun <T : Any> invoke(
        req: TopicAppendEntryReq,
        responseHandler: (TopicAppendToSubmitted) -> T
    ): T
}

interface TopicCreatePort {
    operator fun <T : Any> invoke(
        flowId: FlowId,
        req: TopicCreateReq,
        responseHandler: (TopicCreateSubmitted) -> T
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
    private val eventBrokerRepository: BrokerRepository,
    private val generateDomainId: GenerateDomainId,
    private val flowQueryRepository: FlowQueryRepository,
    private val reqCmdRepository: ReqCmdRepository
) : TopicPort {

    override fun <T : Any> invoke(
        req: TopicAppendEntryReq,
        responseHandler: (TopicAppendToSubmitted) -> T
    ): T {
        val topic = eventBrokerRepository.getTopic(req.topicId)
        return TopicAppendToSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            groupId = topic.groupId,
            topicId = req.topicId,
            payload = req.payload
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        flowId: FlowId,
        req: TopicCreateReq,
        responseHandler: (TopicCreateSubmitted) -> T
    ): T {
        val flow = flowQueryRepository.get(flowId)
        return TopicCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            groupId = flow.groupId,
            topicId = generateDomainId(::TopicId),
            flowId = flow.id,
            name = req.name
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(topicId: TopicId, responseHandler: (Topic) -> T): T =
        responseHandler(eventBrokerRepository.getTopic(topicId))

    override fun <T : Any> invoke(query: TopicQuery, responseHandler: (List<Topic>) -> T): T =
        responseHandler(eventBrokerRepository.listTopics(query))

    override fun <T : Any> invoke(
        topicId: TopicId,
        query: TopicEntryQuery,
        responseHandler: (List<TopicEntry>, Topic) -> T
    ): T {
        val topic = eventBrokerRepository.getTopic(topicId)
        return responseHandler(eventBrokerRepository.listEntries(topic, query), topic)
    }
}