package io.hamal.api.http.controller.endpoint

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.EndpointCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.sdk.api.ApiEndpointCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.lib.domain.request.EndpointCreateRequested
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
        @RequestBody req: ApiEndpointCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        createEndpoint(flowId, req, EndpointCreateRequested::accepted)
    }
}