package io.hamal.backend.instance.web.func

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.lib.domain.req.UpdateFuncReq
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithDomainId
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UpdateFuncRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val request: SubmitRequest,
) {
    @PutMapping("/v1/funcs/{funcId}")
    fun createFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody updateFunc: UpdateFuncReq
    ): ResponseEntity<ApiSubmittedReqWithDomainId> {
        ensureFuncExists(funcId)
        val result = request(funcId, updateFunc)
        return ResponseEntity(result.let {
            ApiSubmittedReqWithDomainId(
                reqId = it.reqId,
                status = it.status,
                id = it.id.value
            )
        }, ACCEPTED)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}