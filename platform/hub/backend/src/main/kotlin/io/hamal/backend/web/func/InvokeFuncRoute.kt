package io.hamal.backend.web.func

import io.hamal.backend.req.SubmitRequest
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.lib.domain.req.InvokeFuncReq
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId
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
    ): ResponseEntity<ApiSubmittedReqWithId> {
        val func = funcQueryRepository.get(funcId)
        val result = request(func.id, invocation)
        return ResponseEntity(result.let {
            ApiSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }
}