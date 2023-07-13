package io.hamal.backend.instance.web.state

import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.StateQueryRepository
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class GetStateRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val stateQueryRepository: StateQueryRepository
) {
    @GetMapping("/v1/funcs/{funcId}/state/{correlationId}")
    fun getState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
    ): ResponseEntity<CorrelatedState> {
        ensureFuncExists(funcId)

        val result = stateQueryRepository.get(Correlation(correlationId, funcId))
        return ResponseEntity.ok(result)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}