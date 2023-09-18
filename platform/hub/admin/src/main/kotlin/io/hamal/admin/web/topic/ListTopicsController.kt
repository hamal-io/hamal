package io.hamal.admin.web.topic

import io.hamal.core.adapter.ListTopicsPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sdk.admin.AdminTopicList
import io.hamal.lib.sdk.admin.AdminTopicList.Topic
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListTopicsController(private val listTopics: ListTopicsPort) {
    @GetMapping("/v1/topics")
    fun listGroupTopics(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: TopicId,
        @RequestParam(required = false, name = "names", defaultValue = "") topicNames: List<TopicName>,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "group_ids", defaultValue = "") groupIds: List<GroupId>
    ): ResponseEntity<AdminTopicList> {
        return listTopics(
            TopicQuery(
                afterId = afterId,
                names = topicNames,
                limit = limit
                // groupid
            )
        ) { topics ->
            ResponseEntity.ok(
                AdminTopicList(
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