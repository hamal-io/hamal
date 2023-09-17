package io.hamal.api.web.state

import io.hamal.core.adapter.state.GetState
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.hub.HubCorrelatedState
import io.hamal.lib.sdk.hub.HubCorrelation
import io.hamal.lib.sdk.hub.HubState
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetStateController(private val getState: GetState) {
    @GetMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun getState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
    ) = getState(funcId, correlationId) { correlatedState, func ->
        ResponseEntity.ok(
            HubCorrelatedState(
                correlation = HubCorrelation(
                    correlationId = correlationId,
                    func = HubCorrelation.Func(
                        id = func.id,
                        name = func.name
                    )
                ),
                state = HubState(correlatedState.value.value)
            )
        )
    }
}