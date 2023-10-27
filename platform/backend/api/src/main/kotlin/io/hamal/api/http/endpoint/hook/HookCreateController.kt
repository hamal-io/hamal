package io.hamal.api.http.endpoint.hook

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.CreateHookPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiCreateHookReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class HookCreateController(
    private val retry: Retry,
    private val createHook: CreateHookPort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/hooks")
    fun createHook(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiCreateHookReq
    ): ResponseEntity<ApiSubmittedReq> = retry {
        createHook(namespaceId, req) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }
}