package io.hamal.api.http.endpoint.topic

import io.hamal.core.adapter.ListTopicsPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sdk.api.ApiTopicList
import io.hamal.lib.sdk.api.ApiTopicList.Topic
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TopicListController(private val listTopics: ListTopicsPort) {
    @GetMapping("/v1/topics")
    fun listGroupTopics(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: TopicId,
        @RequestParam(required = false, name = "names", defaultValue = "") topicNames: List<TopicName>,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "group_ids", defaultValue = "") groupIds: List<GroupId> = listOf(),
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId> = listOf()
    ): ResponseEntity<ApiTopicList> {
        return listTopics(
            TopicQuery(
                afterId = afterId,
                names = topicNames,
                limit = limit,
                groupIds = groupIds,
                namespaceIds = namespaceIds
            )
        ) { topics ->
            ResponseEntity.ok(
                ApiTopicList(
                    topics = topics.map { topic ->
                        Topic(
                            id = topic.id,
                            name = topic.name
                        )
                    }
                )
            )
        }
    }
}