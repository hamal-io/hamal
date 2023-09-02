package io.hamal.backend.web.func

import io.hamal.backend.req.SubmitRequest
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.lib.domain.req.UpdateFuncReq
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId
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
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @PutMapping("/v1/funcs/{funcId}")
    fun createFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody updateFunc: UpdateFuncReq
    ): ResponseEntity<ApiSubmittedReqWithId> {
        ensureFuncExists(funcId)
        ensureNamespaceIdExists(updateFunc)
        val result = request(funcId, updateFunc)
        return ResponseEntity(result.let {
            ApiSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, ACCEPTED)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }

    private fun ensureNamespaceIdExists(updateFunc: UpdateFuncReq) {
        updateFunc.namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}