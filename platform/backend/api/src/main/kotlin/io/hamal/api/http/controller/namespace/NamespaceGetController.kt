package io.hamal.api.http.controller.namespace

import io.hamal.core.adapter.NamespaceGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiNamespace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceGetController(
    private val retry: Retry,
    private val getNamespace: NamespaceGetPort
) {
    @GetMapping("/v1/namespaces/{namespaceId}")
    fun getNamespace(@PathVariable("namespaceId") namespaceId: NamespaceId) = retry {
        getNamespace(namespaceId) { namespace ->
            ResponseEntity.ok(
                ApiNamespace(
                    id = namespace.id,
                    name = namespace.name,
                    inputs = namespace.inputs,
                    type = namespace.type
                )
            )
        }
    }
}