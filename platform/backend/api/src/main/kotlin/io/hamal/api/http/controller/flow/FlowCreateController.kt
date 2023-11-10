package io.hamal.api.http.controller.flow

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.FlowCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiFlowCreateReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.FlowCreateSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FlowCreateController(
    private val retry: Retry,
    private val createFlow: FlowCreatePort
) {
    @PostMapping("/v1/groups/{groupId}/flows")
    fun createFlow(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiFlowCreateReq
    ): ResponseEntity<ApiSubmitted> = retry {
        createFlow(groupId, req, FlowCreateSubmitted::accepted)
    }
}