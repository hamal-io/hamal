package io.hamal.backend.instance.web.state

import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.StateQueryRepository
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiCorrelatedState
import io.hamal.lib.sdk.domain.ApiCorrelation
import io.hamal.lib.sdk.domain.ApiState
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetStateRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val stateQueryRepository: StateQueryRepository
) {
    @GetMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun getState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
    ): ResponseEntity<ApiCorrelatedState> {
        val func = funcQueryRepository.get(funcId)
        val result = stateQueryRepository.get(Correlation(correlationId, funcId))
        return ResponseEntity.ok(
            ApiCorrelatedState(
                correlation = ApiCorrelation(
                    correlationId = correlationId,
                    func = ApiCorrelation.ApiFunc(
                        id = func.id,
                        name = func.name
                    )
                ),
                value = ApiState(result.value.value)
            )
        )
    }
}