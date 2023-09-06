package io.hamal.api.web.func

import io.hamal.api.req.SubmitApiRequest
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.hub.HubInvokeFuncReq
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import io.hamal.repository.api.FuncQueryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class InvokeFuncRoute(
    private val request: SubmitApiRequest,
    private val funcQueryRepository: FuncQueryRepository
) {
    @PostMapping("/v1/funcs/{funcId}/exec")
    fun execFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody invocation: HubInvokeFuncReq
    ): ResponseEntity<HubSubmittedReqWithId> {
        val func = funcQueryRepository.get(funcId)
        val result = request(func.id, invocation)
        return ResponseEntity(result.let {
            HubSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, HttpStatus.ACCEPTED)
    }
}