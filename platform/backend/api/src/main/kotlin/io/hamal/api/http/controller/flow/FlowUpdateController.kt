package io.hamal.api.http.controller.flow

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.FlowUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.FlowUpdateRequested
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.sdk.api.ApiFlowUpdateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FlowUpdateController(
    private val retry: Retry,
    private val updateFlow: FlowUpdatePort
) {
    @PatchMapping("/v1/flows/{flowId}")
    fun updateFlow(
        @PathVariable("flowId") flowId: FlowId,
        @RequestBody req: ApiFlowUpdateRequest
    ): ResponseEntity<ApiRequested> = retry {
        updateFlow(flowId, req, FlowUpdateRequested::accepted)
    }
}