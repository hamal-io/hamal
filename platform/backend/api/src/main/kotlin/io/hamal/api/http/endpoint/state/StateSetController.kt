package io.hamal.api.http

import io.hamal.core.adapter.StateSetPort
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiState
import io.hamal.lib.sdk.api.ApiStateSetReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class StateSetController(private val setState: StateSetPort) {
    @PutMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun setState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
        @RequestBody state: ApiState
    ): ResponseEntity<ApiSubmittedReqImpl<FuncId>> = setState(
        ApiStateSetReq(
            correlation = Correlation(
                funcId = funcId,
                correlationId = correlationId,
            ),
            value = State(state.value)
        )
    ) {
        ResponseEntity
            .accepted()
            .body(
                ApiSubmittedReqImpl(
                    reqId = it.reqId,
                    status = it.status,
                    namespaceId = null,
                    groupId = it.groupId,
                    id = it.state.correlation.funcId
                )
            )
    }
}