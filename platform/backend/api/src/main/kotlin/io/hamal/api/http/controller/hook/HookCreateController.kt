package io.hamal.api.http.controller.hook

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.HookCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.HookCreateRequested
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.sdk.api.ApiHookCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class HookCreateController(
    private val retry: Retry,
    private val createHook: HookCreatePort
) {
    @PostMapping("/v1/flows/{flowId}/hooks")
    fun createHook(
        @PathVariable("flowId") flowId: FlowId,
        @RequestBody req: ApiHookCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        createHook(flowId, req, HookCreateRequested::accepted)
    }
}