package io.hamal.api.http.controller.endpoint

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.EndpointCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.sdk.api.ApiEndpointCreateReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.lib.domain.submitted.EndpointCreateSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EndpointCreateController(
    private val retry: Retry,
    private val createEndpoint: EndpointCreatePort
) {
    @PostMapping("/v1/flows/{flowId}/endpoints")
    fun createEndpoint(
        @PathVariable("flowId") flowId: FlowId,
        @RequestBody req: ApiEndpointCreateReq
    ): ResponseEntity<ApiSubmitted> = retry {
        createEndpoint(flowId, req, EndpointCreateSubmitted::accepted)
    }
}