package io.hamal.admin.web.namespace

import io.hamal.core.adapter.GetNamespacePort
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.admin.AdminNamespace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetNamespaceController(private val getNamespace: GetNamespacePort) {
    @GetMapping("/v1/namespaces/{namespaceId}")
    fun getNamespace(@PathVariable("namespaceId") namespaceId: NamespaceId) =
        getNamespace(namespaceId) { namespace ->
            ResponseEntity.ok(
                AdminNamespace(
                    id = namespace.id,
                    name = namespace.name,
                    inputs = namespace.inputs
                )
            )
        }
}