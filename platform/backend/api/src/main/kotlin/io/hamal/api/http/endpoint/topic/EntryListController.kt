package io.hamal.api.http.endpoint.topic

import io.hamal.core.adapter.ListTopicEntriesPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.TopicEntryId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.api.ApiTopicEntryList
import io.hamal.repository.api.log.BrokerRepository.TopicEntryQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EntryListController(private val listTopicEntries: ListTopicEntriesPort) {
    @GetMapping("/v1/topics/{topicId}/entries")
    fun listEvents(
        @PathVariable("topicId") topicId: TopicId,
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: TopicEntryId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiTopicEntryList> {
        return listTopicEntries(
            topicId, TopicEntryQuery(
                afterId = afterId,
                limit = limit
            )
        ) { entries, topic ->
            ResponseEntity.ok(
                ApiTopicEntryList(
                    topicId = topic.id,
                    topicName = topic.name,
                    entries = entries.map {
                        ApiTopicEntryList.Entry(
                            id = it.id,
                            payload = it.payload
                        )
                    }
                )
            )
        }
    }
}