package io.hamal.api.http.controller.state

import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.adapter.state.StateGetPort
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
internal class StateGetController(
    private val stateGet: StateGetPort, private val funcGet: FuncGetPort
) {
    @GetMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun getState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
    ): ResponseEntity<ApiCorrelatedState> = stateGet(funcId, correlationId).let { correlatedState ->
        val func = funcGet(correlatedState.correlation.funcId)
        ResponseEntity.ok(
            ApiCorrelatedState(
                correlation = ApiCorrelation(
                    id = correlationId, func = ApiCorrelation.Func(
                        id = func.id, name = func.name
                    )
                ), state = ApiState(correlatedState.value.value)
            )
        )
    }
}