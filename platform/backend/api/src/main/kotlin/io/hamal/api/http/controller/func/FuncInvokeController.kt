package io.hamal.api.http.controller.func

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.FuncInvokePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.Invocation
import io.hamal.lib.sdk.api.ApiFuncInvokeRequest
import io.hamal.lib.sdk.api.ApiRequested
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
        @RequestBody req: ApiFuncInvokeRequest
    ): ResponseEntity<ApiRequested> = retry {
        invokeFunc(
            funcId,
            req.copy(correlationId = req.correlationId ?: CorrelationId.default),
            Invocation.Func,
            ExecInvokeRequested::accepted,
        )
    }
}