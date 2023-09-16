package io.hamal.api.web.topic

import io.hamal.core.component.topic.GetTopic
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.hub.HubTopic
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetTopicController(private val getTopic: GetTopic) {
    @GetMapping("/v1/topics/{topicId}")
    fun getTopic(
        @PathVariable("topicId") topicId: TopicId
    ): ResponseEntity<HubTopic> {
        return getTopic(topicId) {
            ResponseEntity.ok(
                HubTopic(
                    id = it.id,
                    name = it.name
                )
            )
        }
    }
}