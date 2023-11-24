package io.hamal.api.http.controller.endpoint

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.EndpointUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.lib.sdk.api.ApiUpdateEndpointReq
import io.hamal.repository.api.submitted_req.EndpointUpdateSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EndpointUpdateController(
    private val retry: Retry,
    private val updateEndpoint: EndpointUpdatePort
) {
    @PatchMapping("/v1/endpoints/{endpointId}")
    fun createEndpoint(
        @PathVariable("endpointId") endpointId: EndpointId,
        @RequestBody req: ApiUpdateEndpointReq
    ): ResponseEntity<ApiSubmitted> = retry {
        updateEndpoint(endpointId, req, EndpointUpdateSubmitted::accepted)
    }
}