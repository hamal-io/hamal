package io.hamal.api.web.namespace

import io.hamal.core.component.namespace.GetNamespace
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.hub.HubNamespace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetNamespaceController(private val getNamespace: GetNamespace) {
    @GetMapping("/v1/namespaces/{namespaceId}")
    fun getNamespace(@PathVariable("namespaceId") namespaceId: NamespaceId) =
        getNamespace(namespaceId) { namespace ->
            ResponseEntity.ok(
                HubNamespace(
                    id = namespace.id,
                    name = namespace.name,
                    inputs = namespace.inputs
                )
            )
        }
}