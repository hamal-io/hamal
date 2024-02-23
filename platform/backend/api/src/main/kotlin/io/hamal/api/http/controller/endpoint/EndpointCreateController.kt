package io.hamal.api.http.controller.endpoint

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.endpoint.EndpointCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiEndpointCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EndpointCreateController(
    private val retry: Retry,
    private val endpointCreate: EndpointCreatePort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/endpoints")
    fun create(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiEndpointCreateRequest
    ): ResponseEntity<ApiRequested> = retry { endpointCreate(namespaceId, req).accepted() }
}