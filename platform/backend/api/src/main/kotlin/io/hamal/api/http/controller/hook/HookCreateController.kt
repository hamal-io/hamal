package io.hamal.api.http.controller.hook

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.HookCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiHookCreateReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.HookCreateSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class HookCreateController(
    private val retry: Retry,
    private val createHook: HookCreatePort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/hooks")
    fun createHook(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiHookCreateReq
    ): ResponseEntity<ApiSubmitted> = retry {
        createHook(namespaceId, req, HookCreateSubmitted::accepted)
    }
}