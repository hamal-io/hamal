package io.hamal.api.web.namespace

import io.hamal.api.web.req.Assembler
import io.hamal.core.component.namespace.CreateNamespace
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubCreateNamespaceReq
import io.hamal.lib.sdk.hub.HubSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateNamespaceController(private val createNamespace: CreateNamespace) {
    @PostMapping("/v1/groups/{groupId}/namespaces")
    fun createNamespace(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: HubCreateNamespaceReq
    ): ResponseEntity<HubSubmittedReq> =
        createNamespace(groupId, req) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
}