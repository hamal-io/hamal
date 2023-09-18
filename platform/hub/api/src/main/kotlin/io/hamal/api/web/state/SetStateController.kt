package io.hamal.api.web.state

import io.hamal.core.adapter.SetStatePort
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.hub.HubDefaultSubmittedReq
import io.hamal.lib.sdk.hub.HubSetStateReq
import io.hamal.lib.sdk.hub.HubState
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class SetStateController(private val setState: SetStatePort) {
    @PostMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun setState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
        @RequestBody state: HubState
    ) = setState(
        HubSetStateReq(
            correlation = Correlation(
                funcId = funcId,
                correlationId = correlationId,
            ),
            value = State(state.value)
        )
    ) {
        ResponseEntity(
            HubDefaultSubmittedReq(
                reqId = it.reqId,
                status = it.status,
                groupId = it.groupId,
            ), HttpStatus.ACCEPTED
        )
    }
}