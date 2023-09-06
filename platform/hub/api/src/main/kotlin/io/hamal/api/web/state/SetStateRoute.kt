package io.hamal.api.web

import io.hamal.api.req.SubmitApiRequest
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.hub.HubDefaultSubmittedReq
import io.hamal.lib.sdk.hub.HubSetStateReq
import io.hamal.lib.sdk.hub.HubState
import io.hamal.lib.sdk.hub.HubSubmittedReq
import io.hamal.repository.api.FuncQueryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class SetStateRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val request: SubmitApiRequest
) {
    @PostMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun setState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
        @RequestBody state: HubState
    ): ResponseEntity<HubSubmittedReq> {
        ensureFuncExists(funcId)

        val result = request(
            HubSetStateReq(
                correlation = Correlation(
                    funcId = funcId,
                    correlationId = correlationId,
                ),
                value = State(state.value)
            )
        )
        return ResponseEntity(result.let {
            HubDefaultSubmittedReq(
                reqId = it.reqId,
                status = it.status
            )
        }, HttpStatus.ACCEPTED)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}