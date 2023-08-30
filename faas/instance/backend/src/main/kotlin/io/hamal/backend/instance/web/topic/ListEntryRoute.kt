package io.hamal.backend.instance.web.topic

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.TopicEntryId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.domain.ApiTopicEntryList
import io.hamal.repository.api.log.LogBrokerRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListEntryRoute(
    private val eventBrokerRepository: LogBrokerRepository
) {
    @GetMapping("/v1/topics/{topicId}/entries")
    fun listEvents(
        @PathVariable("topicId") topicId: TopicId,
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: TopicEntryId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiTopicEntryList> {
        val topic = eventBrokerRepository.getTopic(topicId)
        val events = eventBrokerRepository.listEntries(topic) {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(
            ApiTopicEntryList(
                topicId = topic.id,
                topicName = topic.name,
                entries = events.map {
                    ApiTopicEntryList.Entry(
                        id = it.id,
                        payload = it.payload
                    )
                }
            )
        )
    }
}