package io.hamal.api.http.controller.func

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.FuncInvokePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.EmptyInvocation
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.sdk.api.ApiInvokeFuncVersionReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.ExecInvokeSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncInvokeController(
    private val retry: Retry,
    private val invokeFunc: FuncInvokePort,
) {
    @PostMapping("/v1/funcs/{funcId}/invoke")
    fun execFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody req: ApiInvokeFuncVersionReq? = null
    ): ResponseEntity<ApiSubmitted> = retry {

        invokeFunc(
            funcId,
            ApiInvokeFuncVersionReq(
                correlationId = req?.correlationId ?: CorrelationId.default,
                inputs = req?.inputs ?: InvocationInputs(),
                invocation = EmptyInvocation,
                version = req?.version
            ),
            ExecInvokeSubmitted::accepted
        )
    }
}