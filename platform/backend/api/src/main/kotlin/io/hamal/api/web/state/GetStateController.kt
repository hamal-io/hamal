package io.hamal.api.web.state

import io.hamal.core.adapter.GetStatePort
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiCorrelatedState
import io.hamal.lib.sdk.api.ApiCorrelation
import io.hamal.lib.sdk.api.ApiState
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetStateController(private val getState: GetStatePort) {
    @GetMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun getState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
    ) = getState(funcId, correlationId) { correlatedState, func ->
        ResponseEntity.ok(
            ApiCorrelatedState(
                correlation = ApiCorrelation(
                    correlationId = correlationId,
                    func = ApiCorrelation.Func(
                        id = func.id,
                        name = func.name
                    )
                ),
                state = ApiState(correlatedState.value.value)
            )
        )
    }
}