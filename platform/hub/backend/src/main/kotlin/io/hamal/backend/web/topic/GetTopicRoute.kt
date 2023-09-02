package io.hamal.backend.web.topic

import io.hamal.repository.api.log.BrokerRepository
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.domain.ApiTopic
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetTopicRoute(
    private val eventBrokerRepository: BrokerRepository
) {
    @GetMapping("/v1/topics/{topicId}")
    fun getTopic(
        @PathVariable("topicId") topicId: TopicId
    ): ResponseEntity<ApiTopic> {
        val topic = eventBrokerRepository.getTopic(topicId)
        return ResponseEntity.ok(
            ApiTopic(
                id = topic.id,
                name = topic.name
            )
        )
    }
}