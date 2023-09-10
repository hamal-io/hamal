package io.hamal.admin.web

import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.admin.AdminDefaultSubmittedReq
import io.hamal.lib.sdk.admin.AdminSetStateReq
import io.hamal.lib.sdk.admin.AdminState
import io.hamal.lib.sdk.admin.AdminSubmittedReq
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
    private val request: SubmitAdminRequest
) {
    @PostMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun setState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
        @RequestBody state: AdminState
    ): ResponseEntity<AdminSubmittedReq> {
        ensureFuncExists(funcId)

        val result = request(
            AdminSetStateReq(
                correlation = Correlation(
                    funcId = funcId,
                    correlationId = correlationId,
                ),
                value = State(state.value)
            )
        )
        return ResponseEntity(result.let {
            AdminDefaultSubmittedReq(
                reqId = it.reqId,
                status = it.status
            )
        }, HttpStatus.ACCEPTED)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}