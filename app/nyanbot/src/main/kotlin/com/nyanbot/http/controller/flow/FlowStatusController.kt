package com.nyanbot.http.controller.flow

import com.nyanbot.repository.FlowCmdRepository.*
import com.nyanbot.repository.FlowId
import com.nyanbot.repository.FlowRepository
import com.nyanbot.repository.FlowStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FlowStatusController(
    private val flowRepository: FlowRepository
) {
    @PostMapping("/v1/flows/{flowId}/activate")
    fun activate(
        @PathVariable("flowId") flowId: FlowId
    ): FlowStatusResponse {
        val response = flowRepository.set(flowId, SetStatusCmd(FlowStatus.Active))
        return FlowStatusResponse(
            id = response.id,
            status = response.status,
        )
    }

    @PostMapping("/v1/flows/{flowId}/deactivate")
    fun deactivate(
        @PathVariable("flowId") flowId: FlowId
    ): FlowStatusResponse {
        val response = flowRepository.set(flowId, SetStatusCmd(FlowStatus.Inactive))
        return FlowStatusResponse(
            id = response.id,
            status = response.status,
        )

    }

    data class FlowStatusResponse(
        val id: FlowId,
        val status: FlowStatus
    )
}