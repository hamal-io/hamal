package io.hamal.admin.web.func

import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import io.hamal.lib.sdk.admin.AdminUpdateFuncReq
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class UpdateFuncRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val request: SubmitAdminRequest,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @PutMapping("/v1/funcs/{funcId}")
    fun createFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody updateFunc: AdminUpdateFuncReq
    ): ResponseEntity<AdminSubmittedReqWithId> {
        ensureFuncExists(funcId)
        ensureNamespaceIdExists(updateFunc)
        val result = request(funcId, updateFunc)
        return ResponseEntity(result.let {
            AdminSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, ACCEPTED)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }

    private fun ensureNamespaceIdExists(updateFunc: AdminUpdateFuncReq) {
        updateFunc.namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}