package io.hamal.backend.instance.web.event

import io.hamal.backend.instance.service.query.EventQueryService
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.Limit
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.domain.*
import io.hamal.lib.sdk.domain.ListTopicsResponse.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class ListEventsRoute<TOPIC : LogTopic>(
    private val queryService: EventQueryService<TOPIC>
) {
    @GetMapping("/v1/topics/{topicId}/events")
    fun listEvents(
        @PathVariable("topicId") topicId: TopicId,
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: EventId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ListEventsResponse> {
        val topic = queryService.getTopic(topicId)
        val events = queryService.listEvents(topic) {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(
            ListEventsResponse(
                topicId = topic.id,
                topicName = topic.name,
                events = events
            )
        )
    }
}