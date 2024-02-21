package io.hamal.api.http.controller.topic

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
    private val topicGet: TopicGetPort
) {
    @GetMapping("/v1/topics/{topicId}")
    fun get(
        @PathVariable("topicId") topicId: TopicId
    ): ResponseEntity<ApiTopic> = retry {
        topicGet(topicId).let {
            ResponseEntity.ok(
                ApiTopic(
                    id = it.id,
                    name = it.name,
                    type = it.type
                )
            )
        }
    }
}