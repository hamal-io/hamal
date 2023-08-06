package io.hamal.backend.instance.web.func

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.lib.domain.req.SubmittedUpdateFuncReq
import io.hamal.lib.domain.req.UpdateFuncReq
import io.hamal.lib.domain.vo.FuncId
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
    ): ResponseEntity<SubmittedUpdateFuncReq> {
        ensureFuncExists(funcId)
        val result = request(funcId, updateFunc)
        return ResponseEntity(result, ACCEPTED)
    }

    private fun ensureFuncExists(funcId: FuncId) {
        funcQueryRepository.get(funcId)
    }
}