package io.hamal.api.http.endpoint.snippet

import io.hamal.core.adapter.SnippetCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.sdk.api.ApiCreateSnippetReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
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
    ): ResponseEntity<ApiSubmittedReqImpl<SnippetId>> = retry {
        createSnippet(groupId, accountId = AccountId(1), req) {
            ResponseEntity
                .accepted()
                .body(
                    ApiSubmittedReqImpl(
                        reqId = it.reqId,
                        status = it.status,
                        namespaceId = null,
                        groupId = it.groupId,
                        id = it.id
                    )
                )
        }
    }
}