package io.hamal.backend.web.event

import io.hamal.backend.service.query.EventQueryService
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.domain.*
import io.hamal.lib.sdk.domain.ListTopicsResponse.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class ListEventsRoute(
    @Autowired private val queryService: EventQueryService<*>
) {
    @GetMapping("/v1/topics/{topicId}/events")
    fun listEvents(
        @PathVariable("topicId") topicId: String,
        @RequestParam(required = false, name = "stringEvtId", defaultValue = "0") stringEvtId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ListEventsResponse> {
        val topicId = TopicId(SnowflakeId(topicId.toLong()))
        val topic = requireNotNull(queryService.findTopic(topicId)) { "Topic with $topicId not found" }

        return ResponseEntity.ok(
            ListEventsResponse(
                topicId = topic.id,
                topicName = topic.name,
                events = queryService.queryEvents(
                    EventQueryService.EventQuery(topic.id)
                ).map {
                    ListEventsResponse.Event(
                        contentType = it.contentType,
                        content = it.content
                    )
                }
            )
        )
    }

}