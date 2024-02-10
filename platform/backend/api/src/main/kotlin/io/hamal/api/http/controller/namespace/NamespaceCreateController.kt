package io.hamal.api.http.controller.namespace

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.NamespaceCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.NamespaceCreateRequested
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiNamespaceCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
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
        @RequestBody req: ApiNamespaceCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        createNamespace(groupId, req, NamespaceCreateRequested::accepted)
    }
}