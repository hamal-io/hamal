package io.hamal.backend.instance.web.func

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.lib.domain.req.InvokeFuncReq
import io.hamal.lib.domain.req.SubmittedInvokeExecReq
import io.hamal.lib.domain.vo.FuncId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class InvokeFuncRoute(
    private val request: SubmitRequest,
    private val funcQueryRepository: FuncQueryRepository
) {
    @PostMapping("/v1/funcs/{funcId}/exec")
    fun execFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody invocation: InvokeFuncReq
    ): ResponseEntity<SubmittedInvokeExecReq> {
        val func = funcQueryRepository.get(funcId)
        val result = request(func.id, invocation)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }
}