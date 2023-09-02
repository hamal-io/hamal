package io.hamal.backend.instance.web.topic

import io.hamal.repository.api.log.BrokerRepository
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sdk.domain.ApiTopicList
import io.hamal.lib.sdk.domain.ApiTopicList.Topic
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListTopicRoute(
    private val eventBrokerRepository: BrokerRepository
) {
    @GetMapping("/v1/topics")
    fun listTopics(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: TopicId,
        @RequestParam(required = false, name = "names", defaultValue = "") topicNames: List<TopicName>,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiTopicList> {
        val topics = eventBrokerRepository.listTopics {
            this.afterId = afterId
            this.names = topicNames
            this.limit = limit
        }
        return ResponseEntity.ok(
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