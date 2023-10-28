package io.hamal.api.http.endpoint.snippet

import io.hamal.api.http.endpoint.accepted
import io.hamal.core.adapter.SnippetCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiCreateSnippetReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.SnippetCreateSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SnippetCreateController(
    private val retry: Retry,
    private val createSnippet: SnippetCreatePort
) {
    @PostMapping("/v1/groups/{groupId}/snippets")
    fun createSnippet(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiCreateSnippetReq
    ): ResponseEntity<ApiSubmitted> = retry {
        createSnippet(groupId, AccountId(1), req, SnippetCreateSubmitted::accepted)
    }
}