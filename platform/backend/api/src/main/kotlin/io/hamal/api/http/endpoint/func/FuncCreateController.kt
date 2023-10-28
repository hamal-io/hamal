package io.hamal.api.http.endpoint.func

import io.hamal.api.http.endpoint.accepted
import io.hamal.core.adapter.FuncCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiFuncCreateReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.FuncCreateSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncCreateController(
    private val retry: Retry,
    private val createFunc: FuncCreatePort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/funcs")
    fun createFunc(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiFuncCreateReq
    ): ResponseEntity<ApiSubmitted> = retry {
        createFunc(namespaceId, req, FuncCreateSubmitted::accepted)
    }
}