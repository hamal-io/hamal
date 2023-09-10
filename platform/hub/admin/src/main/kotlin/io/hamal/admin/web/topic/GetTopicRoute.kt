package io.hamal.admin.web.topic

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.admin.AdminTopic
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetTopicRoute(
    private val eventBrokerRepository: BrokerRepository
) {
    @GetMapping("/v1/topics/{topicId}")
    fun getTopic(
        @PathVariable("topicId") topicId: TopicId
    ): ResponseEntity<AdminTopic> {
        val topic = eventBrokerRepository.getTopic(topicId)
        return ResponseEntity.ok(
            AdminTopic(
                id = topic.id,
                name = topic.name
            )
        )
    }
}