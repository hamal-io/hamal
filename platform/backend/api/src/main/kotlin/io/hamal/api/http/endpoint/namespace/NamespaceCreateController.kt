package io.hamal.api.http.endpoint.namespace

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.NamespaceCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiNamespaceCreateReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceCreateController(
    private val retry: Retry,
    private val createNamespace: NamespaceCreatePort
) {
    @PostMapping("/v1/groups/{groupId}/namespaces")
    fun createNamespace(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiNamespaceCreateReq
    ): ResponseEntity<ApiSubmittedReq> = retry {
        createNamespace(groupId, req) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }
}