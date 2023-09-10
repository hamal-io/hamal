package io.hamal.admin.web.func

import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.admin.AdminInvokeFuncReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import io.hamal.repository.api.FuncQueryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class InvokeFuncRoute(
    private val request: SubmitAdminRequest,
    private val funcQueryRepository: FuncQueryRepository
) {
    @PostMapping("/v1/funcs/{funcId}/exec")
    fun execFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody invocation: AdminInvokeFuncReq
    ): ResponseEntity<AdminSubmittedReqWithId> {
        val func = funcQueryRepository.get(funcId)
        val result = request(func.id, invocation)
        return ResponseEntity(result.let {
            AdminSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }
}