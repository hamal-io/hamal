package io.hamal.api.http.controller.func

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.func.FuncCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiFuncCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncCreateController(
    private val retry: Retry,
    private val funcCreate: FuncCreatePort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/funcs")
    fun create(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiFuncCreateRequest
    ): ResponseEntity<ApiRequested> = retry { funcCreate(namespaceId, req).accepted() }
}