package io.hamal.admin.web.topic

import io.hamal.core.adapter.topic.ListTopics
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sdk.admin.AdminTopicList
import io.hamal.lib.sdk.admin.AdminTopicList.Topic
import io.hamal.repository.api.log.BrokerTopicsRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListTopicsController(private val listTopics: ListTopics) {
    @GetMapping("/v1/groups/{groupId}/topics")
    fun listGroupTopics(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: TopicId,
        @RequestParam(required = false, name = "names", defaultValue = "") topicNames: List<TopicName>,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<AdminTopicList> {
        return listTopics(
            BrokerTopicsRepository.TopicQuery(
                afterId = afterId,
                names = topicNames,
                limit = limit
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