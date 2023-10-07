package io.hamal.api.web.topic

import io.hamal.api.web.req.Assembler
import io.hamal.core.adapter.CreateTopicPort
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiCreateTopicReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateTopicController(private val createTopic: CreateTopicPort) {

    @PostMapping("/v1/groups/{groupId}/topics")
    fun createTopic(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiCreateTopicReq
    ): ResponseEntity<ApiSubmittedReq> {
        return createTopic(groupId, req) {
            ResponseEntity(Assembler.assemble(it), HttpStatus.ACCEPTED)
        }
    }
}