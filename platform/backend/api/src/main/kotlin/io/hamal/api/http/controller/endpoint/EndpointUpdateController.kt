package io.hamal.api.http.controller.endpoint

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.endpoint.EndpointUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.sdk.api.ApiEndpointUpdateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EndpointUpdateController(
    private val retry: Retry,
    private val endpointUpdate: EndpointUpdatePort
) {
    @PatchMapping("/v1/endpoints/{endpointId}")
    fun update(
        @PathVariable("endpointId") endpointId: EndpointId,
        @RequestBody req: ApiEndpointUpdateRequest
    ): ResponseEntity<ApiRequested> = retry {
        endpointUpdate(endpointId, req).accepted()
    }
}