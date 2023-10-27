package io.hamal.api.http

import io.hamal.core.adapter.SetStatePort
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiDefaultSubmittedReq
import io.hamal.lib.sdk.api.ApiSetStateReq
import io.hamal.lib.sdk.api.ApiState
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class StateSetController(private val setState: SetStatePort) {
    @PostMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun setState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
        @RequestBody state: ApiState
    ) = setState(
        ApiSetStateReq(
            correlation = Correlation(
                funcId = funcId,
                correlationId = correlationId,
            ),
            value = State(state.value)
        )
    ) {
        ResponseEntity(
            ApiDefaultSubmittedReq(
                reqId = it.reqId,
                status = it.status,
                namespaceId = NamespaceId(1337),
                groupId = it.groupId,
            ), HttpStatus.ACCEPTED
        )
    }
}