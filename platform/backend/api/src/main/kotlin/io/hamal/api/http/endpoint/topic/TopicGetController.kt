package io.hamal.api.http.endpoint.topic

import io.hamal.core.adapter.TopicGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.api.ApiTopic
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TopicGetController(
    private val retry: Retry,
    private val getTopic: TopicGetPort
) {
    @GetMapping("/v1/topics/{topicId}")
    fun getTopic(
        @PathVariable("topicId") topicId: TopicId
    ): ResponseEntity<ApiTopic> = retry {
        getTopic(topicId) {
            ResponseEntity.ok(
                ApiTopic(
                    id = it.id,
                    name = it.name
                )
            )
        }
    }
}