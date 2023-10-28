package io.hamal.api.http.endpoint.snippet

import io.hamal.core.adapter.SnippetUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import io.hamal.lib.sdk.api.ApiUpdateSnippetReq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class SnippetUpdateController(
    private val retry: Retry,
    private val updateSnippet: SnippetUpdatePort
) {
    @PatchMapping("/v1/snippets/{snippetId}")
    fun updateSnippet(
        @PathVariable("snippetId") snippetId: SnippetId,
        @RequestBody req: ApiUpdateSnippetReq
    ): ResponseEntity<ApiSubmittedReqImpl<SnippetId>> = retry {
        updateSnippet(snippetId, req) {
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