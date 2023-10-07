package io.hamal.api.web.topic

import io.hamal.core.adapter.GetTopicPort
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.api.ApiTopic
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetTopicController(private val getTopic: GetTopicPort) {
    @GetMapping("/v1/topics/{topicId}")
    fun getTopic(
        @PathVariable("topicId") topicId: TopicId
    ): ResponseEntity<ApiTopic> {
        return getTopic(topicId) {
            ResponseEntity.ok(
                ApiTopic(
                    id = it.id,
                    name = it.name
                )
            )
        }
    }
}