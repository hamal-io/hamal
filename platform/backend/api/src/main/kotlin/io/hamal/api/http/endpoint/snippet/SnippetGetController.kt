package io.hamal.api.http.endpoint.snippet

import io.hamal.core.adapter.GetSnippetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.sdk.api.ApiSnippet
import io.hamal.repository.api.Snippet
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class SnippetGetController(
    private val retry: Retry,
    private val getSnippet: GetSnippetPort
) {
    @GetMapping("/v1/snippets/{snippetId}")
    fun getSnippet(@PathVariable("snippetId") snippetId: SnippetId) = retry {
        getSnippet(snippetId, ::assemble)
    }

    private fun assemble(snippet: Snippet) =
        ResponseEntity.ok(
            ApiSnippet(
                id = snippet.id,
                name = snippet.name,
                inputs = snippet.inputs,
                value = snippet.value
            )
        )
}