package io.hamal.api.http.endpoint.func

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.InvokeFuncPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.sdk.api.ApiInvokeFuncReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncInvokeController(
    private val retry: Retry,
    private val invokeFunc: InvokeFuncPort
) {
    @PostMapping("/v1/funcs/{funcId}/invoke")
    fun execFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody req: ApiInvokeFuncReq? = null
    ): ResponseEntity<ApiSubmittedReq> = retry {
        invokeFunc(
            funcId, ApiInvokeFuncReq(
                correlationId = req?.correlationId ?: CorrelationId.default,
                inputs = req?.inputs ?: InvocationInputs()
            )
        ) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }
}