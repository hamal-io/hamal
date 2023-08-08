package io.hamal.backend.instance.web.topic

import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.EventId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.domain.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ListEventRoute(
    private val eventBrokerRepository: LogBrokerRepository
) {
    @GetMapping("/v1/topics/{topicId}/events")
    fun listEvents(
        @PathVariable("topicId") topicId: TopicId,
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: EventId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ListEventsResponse> {
        val topic = eventBrokerRepository.getTopic(topicId)
        val events = eventBrokerRepository.listEvents(topic) {
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