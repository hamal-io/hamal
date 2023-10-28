package io.hamal.api.http.endpoint.func

import io.hamal.core.adapter.FuncInvokePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.sdk.api.ApiFuncInvokeReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncInvokeController(
    private val retry: Retry,
    private val invokeFunc: FuncInvokePort
) {
    @PostMapping("/v1/funcs/{funcId}/invoke")
    fun execFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody req: ApiFuncInvokeReq? = null
    ): ResponseEntity<ApiSubmittedReqImpl<ExecId>> = retry {
        invokeFunc(
            funcId, ApiFuncInvokeReq(
                correlationId = req?.correlationId ?: CorrelationId.default,
                inputs = req?.inputs ?: InvocationInputs()
            )
        ) {
            ResponseEntity
                .accepted()
                .body(
                    ApiSubmittedReqImpl(
                        reqId = it.reqId,
                        status = it.status,
                        namespaceId = it.namespaceId,
                        groupId = it.groupId,
                        id = it.id
                    )
                )
        }
    }
}