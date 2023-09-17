package io.hamal.admin.web.topic

import io.hamal.admin.web.req.Assembler
import io.hamal.core.adapter.topic.AppendEntryToTopic
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.admin.AdminSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AppendEntryController(private val appendEntryToTopic: AppendEntryToTopic) {
    @PostMapping("/v1/topics/{topicId}/entries")
    fun appendEvent(
        @PathVariable("topicId") topicId: TopicId,
        @RequestBody topAppend: TopicEntryPayload
    ): ResponseEntity<AdminSubmittedReq> {
        return appendEntryToTopic(topicId, topAppend) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }
}