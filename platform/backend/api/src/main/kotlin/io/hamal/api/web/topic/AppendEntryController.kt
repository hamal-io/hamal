package io.hamal.api.web.topic

import io.hamal.api.web.req.Assembler
import io.hamal.core.adapter.AppendEntryToTopicPort
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AppendEntryController(private val appendEntryToTopic: AppendEntryToTopicPort) {
    @PostMapping("/v1/topics/{topicId}/entries")
    fun appendEvent(
        @PathVariable("topicId") topicId: TopicId,
        @RequestBody topAppend: TopicEntryPayload
    ): ResponseEntity<ApiSubmittedReq> {
        return appendEntryToTopic(topicId, topAppend) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }
}