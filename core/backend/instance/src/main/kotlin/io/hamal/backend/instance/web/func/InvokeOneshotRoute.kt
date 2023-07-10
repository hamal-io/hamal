package io.hamal.backend.instance.web.func

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.lib.domain.req.InvokeOneshotReq
import io.hamal.lib.domain.req.SubmittedInvokeOneshotReq
import io.hamal.lib.domain.vo.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class InvokeOneshotRoute(
    private val request: SubmitRequest,
    private val funcQueryRepository: FuncQueryRepository
) {
    @PostMapping("/v1/funcs/{funcId}/exec")
    fun execFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody oneshot: InvokeOneshotReq
    ): ResponseEntity<SubmittedInvokeOneshotReq> {
        val func = funcQueryRepository.get(funcId)
        val result = request(func.id, oneshot)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }
}