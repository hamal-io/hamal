package com.nyanbot.http.controller.flow

import com.nyanbot.repository.Flow
import com.nyanbot.repository.FlowQueryRepository.FlowQuery
import com.nyanbot.repository.FlowRepository
import io.hamal.lib.common.domain.Limit
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FlowListController(
    private val flowRepository: FlowRepository
) {

    @GetMapping("/v1/flows")
    fun invoke(): List<Flow> {
        return flowRepository.list(
            FlowQuery(
                limit = Limit(1000)
            )
        )

    }
}