package io.hamal.api.web.state

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.hub.HubCorrelatedState
import io.hamal.lib.sdk.hub.HubCorrelation
import io.hamal.lib.sdk.hub.HubState
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.StateQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetStateRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val stateQueryRepository: StateQueryRepository
) {
    @GetMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun getState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
    ): ResponseEntity<HubCorrelatedState> {
        val func = funcQueryRepository.get(funcId)
        val result = stateQueryRepository.get(Correlation(correlationId, funcId))
        return ResponseEntity.ok(
            HubCorrelatedState(
                correlation = HubCorrelation(
                    correlationId = correlationId,
                    func = HubCorrelation.Func(
                        id = func.id,
                        name = func.name
                    )
                ),
                state = HubState(result.value.value)
            )
        )
    }
}