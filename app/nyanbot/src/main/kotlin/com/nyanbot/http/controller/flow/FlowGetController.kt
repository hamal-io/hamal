package com.nyanbot.http.controller.flow

import com.nyanbot.repository.Flow
import com.nyanbot.repository.FlowId
import com.nyanbot.repository.FlowRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FlowGetController(
    private val flowRepository: FlowRepository
) {
    @GetMapping("/v1/flows/{flowId}")
    fun invoke(@PathVariable flowId: FlowId): Flow {
        return flowRepository.get(flowId)
    }
}