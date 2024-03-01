package io.hamal.api.http.controller.state

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.state.StateSetPort
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.lib.sdk.api.ApiState
import io.hamal.lib.sdk.api.ApiStateSetRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class StateSetController(private val stateSet: StateSetPort) {
    @PutMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun set(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
        @RequestBody state: ApiState
    ): ResponseEntity<ApiRequested> = stateSet(
        ApiStateSetRequest(
            correlation = Correlation(
                funcId = funcId,
                id = correlationId,
            ),
            value = State(state.value)
        )
    ).accepted()
}