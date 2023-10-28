package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.api.ApiTopicAppendEntryReq
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.BrokerRepository.TopicEntryQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.Topic
import io.hamal.repository.api.log.TopicEntry
import io.hamal.repository.api.submitted_req.TopicAppendToSubmitted
import io.hamal.repository.api.submitted_req.TopicCreateSubmitted
import io.hamal.request.CreateTopicReq
import org.springframework.stereotype.Component

interface TopicAppendEntryPort {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        topAppend: TopicEntryPayload,
        responseHandler: (TopicAppendToSubmitted) -> T
    ): T
}

interface TopicCreatePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: CreateTopicReq,
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
    private val submitRequest: SubmitRequest,
    private val eventBrokerRepository: BrokerRepository
) : TopicPort {

    override fun <T : Any> invoke(
        topicId: TopicId,
        topAppend: TopicEntryPayload,
        responseHandler: (TopicAppendToSubmitted) -> T
    ): T {
        ensureTopicExists(topicId)
        return responseHandler(submitRequest(ApiTopicAppendEntryReq(topicId, topAppend)))
    }

    override fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: CreateTopicReq,
        responseHandler: (TopicCreateSubmitted) -> T
    ): T =
        responseHandler(submitRequest(namespaceId, req))

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

    private fun ensureTopicExists(topicId: TopicId) = eventBrokerRepository.getTopic(topicId)
}