package io.hamal.api.http.endpoint.snippet

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.UpdateSnippetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.sdk.api.ApiUpdateSnippetReq
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class SnippetUpdateController(
    private val retry: Retry,
    private val updateSnippet: UpdateSnippetPort
) {
    @PatchMapping("/v1/snippets/{snippetId}")
    fun updateSnippet(
        @PathVariable("snippetId") snippetId: SnippetId,
        @RequestBody req: ApiUpdateSnippetReq
    ) = retry {
        updateSnippet(snippetId, req) { submittedReq ->
            ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
        }
    }
}