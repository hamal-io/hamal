package io.hamal.api.http.controller.topic

import io.hamal.core.adapter.TopicEventListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.TopicEventId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.api.ApiTopicEventList
import io.hamal.repository.api.TopicQueryRepository.TopicEventQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TopicEventListController(private val listTopicEvents: TopicEventListPort) {
    @GetMapping("/v1/topics/{topicId}/events")
    fun listEvents(
        @PathVariable("topicId") topicId: TopicId,
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: TopicEventId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiTopicEventList> {
        return listTopicEvents(
            TopicEventQuery(
                topicId = topicId,
                afterId = afterId,
                limit = limit
            )
        ) { events, topic ->
            ResponseEntity.ok(
                ApiTopicEventList(
                    topicId = topic.id,
                    topicName = topic.name,
                    events = events.map {
                        ApiTopicEventList.Event(
                            id = it.id,
                            payload = it.payload
                        )
                    }
                )
            )
        }
    }
}