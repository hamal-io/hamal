package io.hamal.api.http.controller.topic

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.TopicEventAppendPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.TopicEventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.lib.sdk.api.ApiTopicAppendEventRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TopicEventAppendController(
    private val retry: Retry,
    private val topicEventAppend: TopicEventAppendPort
) {
    @PostMapping("/v1/topics/{topicId}/events")
    fun append(
        @PathVariable("topicId") topicId: TopicId,
        @RequestBody topAppend: TopicEventPayload
    ): ResponseEntity<ApiRequested> {
        return retry {
            topicEventAppend(ApiTopicAppendEventRequest(topicId, topAppend)).accepted()
        }
    }
}