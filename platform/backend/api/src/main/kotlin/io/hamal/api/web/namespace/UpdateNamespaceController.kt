package io.hamal.api.web.namespace

import io.hamal.api.web.req.Assembler
import io.hamal.core.adapter.UpdateNamespacePort
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiUpdateNamespaceReq
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class UpdateNamespaceController(private val updateNamespace: UpdateNamespacePort) {
    @PutMapping("/v1/namespaces/{namespaceId}")
    fun createNamespace(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiUpdateNamespaceReq
    ) = updateNamespace(namespaceId, req) {
        ResponseEntity(Assembler.assemble(it), HttpStatus.ACCEPTED)
    }
}