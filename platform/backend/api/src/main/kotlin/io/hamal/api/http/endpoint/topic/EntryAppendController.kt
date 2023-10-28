package io.hamal.api.http.endpoint.topic

import io.hamal.api.http.endpoint.accepted
import io.hamal.core.adapter.TopicAppendEntryPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.TopicAppendToSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EntryAppendController(
    private val retry: Retry,
    private val appendEntryToTopic: TopicAppendEntryPort
) {
    @PostMapping("/v1/topics/{topicId}/entries")
    fun appendEvent(
        @PathVariable("topicId") topicId: TopicId,
        @RequestBody topAppend: TopicEntryPayload
    ): ResponseEntity<ApiSubmitted> {
        return retry {
            appendEntryToTopic(topicId, topAppend, TopicAppendToSubmitted::accepted)
        }
    }
}