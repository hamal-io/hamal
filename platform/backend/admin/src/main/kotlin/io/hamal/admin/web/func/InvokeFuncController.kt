package io.hamal.admin.web.func

import io.hamal.admin.web.req.Assembler
import io.hamal.core.adapter.InvokeFuncPort
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.sdk.admin.AdminInvokeFuncReq
import io.hamal.lib.sdk.admin.AdminSubmittedReq
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class InvokeFuncController(private val invokeFunc: InvokeFuncPort) {
    @PostMapping("/v1/funcs/{funcId}/invoke")
    fun execFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody req: AdminInvokeFuncReq? = null
    ): ResponseEntity<AdminSubmittedReq> =
        invokeFunc(
            funcId, AdminInvokeFuncReq(
                correlationId = req?.correlationId ?: CorrelationId.default,
                inputs = req?.inputs ?: InvocationInputs()
            )
        ) {
            ResponseEntity(Assembler.assemble(it), HttpStatus.ACCEPTED)
        }
}