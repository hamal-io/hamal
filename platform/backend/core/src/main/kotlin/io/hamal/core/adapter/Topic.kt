package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.api.ApiAppendEntryReq
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.BrokerRepository.TopicEntryQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.Topic
import io.hamal.repository.api.log.TopicEntry
import io.hamal.repository.api.submitted_req.SubmittedReqWithGroupId
import io.hamal.request.CreateTopicReq
import org.springframework.stereotype.Component

interface AppendEntryToTopicPort {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        topAppend: TopicEntryPayload,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface CreateTopicPort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateTopicReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T
}

interface GetTopicPort {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        responseHandler: (Topic) -> T
    ): T
}

interface ListTopicEntriesPort {
    operator fun <T : Any> invoke(
        topicId: TopicId,
        query: TopicEntryQuery,
        responseHandler: (List<TopicEntry>, Topic) -> T
    ): T
}

interface ListTopicsPort {
    operator fun <T : Any> invoke(
        query: TopicQuery,
        responseHandler: (List<Topic>) -> T
    ): T
}

interface TopicPort : AppendEntryToTopicPort, CreateTopicPort, GetTopicPort, ListTopicsPort, ListTopicEntriesPort


@Component
class TopicAdapter(
    private val submitRequest: SubmitRequest,
    private val eventBrokerRepository: BrokerRepository
) : TopicPort {

    override fun <T : Any> invoke(
        topicId: TopicId,
        topAppend: TopicEntryPayload,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T {
        ensureTopicExists(topicId)
        return responseHandler(submitRequest(ApiAppendEntryReq(topicId, topAppend)))
    }

    override fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateTopicReq,
        responseHandler: (SubmittedReqWithGroupId) -> T
    ): T =
        responseHandler(submitRequest(groupId, req))

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