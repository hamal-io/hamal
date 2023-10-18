package io.hamal.api.http.endpoint.snippet

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.CreateSnippetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiCreateSnippetReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SnippetCreateController(
    private val retry: Retry,
    private val createSnippet: CreateSnippetPort
) {
    @PostMapping("/v1/groups/{groupId}/snippets")
    fun createSnippet(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiCreateSnippetReq
    ): ResponseEntity<ApiSubmittedReq> = retry {
        createSnippet(groupId, accountId = AccountId(1), req) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }
}