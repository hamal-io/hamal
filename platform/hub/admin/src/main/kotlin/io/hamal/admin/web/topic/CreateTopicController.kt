package io.hamal.admin.web.topic

import io.hamal.admin.web.req.Assembler
import io.hamal.core.component.topic.CreateTopic
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminCreateTopicReq
import io.hamal.lib.sdk.admin.AdminSubmittedReq
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateTopicController(private val createTopic: CreateTopic) {

    @PostMapping("/v1/groups/{groupId}/topics")
    fun createTopic(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: AdminCreateTopicReq
    ): ResponseEntity<AdminSubmittedReq> {
        return createTopic(groupId, req) {
            ResponseEntity(Assembler.assemble(it), HttpStatus.ACCEPTED)
        }
    }
}