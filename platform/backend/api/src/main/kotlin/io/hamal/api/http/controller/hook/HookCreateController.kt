package io.hamal.api.http.controller.hook

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.HookCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiHookCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class HookCreateController(
    private val retry: Retry,
    private val hookCreate: HookCreatePort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/hooks")
    fun create(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiHookCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        hookCreate(namespaceId, req).accepted()
    }
}