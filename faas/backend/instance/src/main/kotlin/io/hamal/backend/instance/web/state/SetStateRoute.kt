package io.hamal.backend.instance.web

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.SetStateReq
import io.hamal.lib.domain.req.SubmittedSetStateReq
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SetStateRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val request: SubmitRequest
) {
    @PostMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun setState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
        @RequestBody state: State
    ): ResponseEntity<SubmittedSetStateReq> {
        ensureFuncExists(funcId)

        val result = request(
            SetStateReq(
                correlation = Correlation(
                    funcId = funcId,
                    correlationId = correlationId,
                ),
                value = state
            )
        )
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}