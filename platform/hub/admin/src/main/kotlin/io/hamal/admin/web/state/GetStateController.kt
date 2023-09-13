package io.hamal.admin.web.state

import io.hamal.core.component.state.GetState
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.admin.AdminCorrelatedState
import io.hamal.lib.sdk.admin.AdminCorrelation
import io.hamal.lib.sdk.admin.AdminState
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
            AdminCorrelatedState(
                correlation = AdminCorrelation(
                    correlationId = correlationId,
                    func = AdminCorrelation.Func(
                        id = func.id,
                        name = func.name
                    )
                ),
                state = AdminState(correlatedState.value.value)
            )
        )
    }
}