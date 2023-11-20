package io.hamal.api.http.controller.flow

import io.hamal.core.adapter.FlowGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.sdk.api.ApiFlow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FlowGetController(
    private val retry: Retry,
    private val getFlow: FlowGetPort
) {
    @GetMapping("/v1/flows/{flowId}")
    fun getFlow(@PathVariable("flowId") flowId: FlowId) = retry {
        getFlow(flowId) { flow ->
            ResponseEntity.ok(
                ApiFlow(
                    id = flow.id,
                    name = flow.name,
                    inputs = flow.inputs,
                    type = flow.type
                )
            )
        }
    }
}