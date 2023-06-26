package io.hamal.backend.instance.web.func

import io.hamal.backend.instance.component.SystemEventEmitter
import io.hamal.backend.instance.req.InvokeOneshot
import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.cmd.FuncCmdService
import io.hamal.backend.instance.service.query.FuncQueryService
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.port.GenerateDomainId
import io.hamal.lib.script.api.value.TableValue
import io.hamal.lib.sdk.domain.ApiExecFuncRequest
import io.hamal.lib.sdk.domain.ApiExecFuncResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class FuncController(
    @Autowired val queryService: FuncQueryService,
    @Autowired val funcCmdService: FuncCmdService,
    @Autowired val execCmdService: ExecCmdService,
    @Autowired val eventEmitter: SystemEventEmitter<*>,
    @Autowired val request: SubmitRequest,
    @Autowired val generateDomainId: GenerateDomainId
) {
    @PostMapping("/v1/funcs/{funcId}/exec")
    fun execFunc(
        @PathVariable("funcId") stringFuncId: String,
        @RequestHeader("X-Correlation-ID", required = false) correlationIdStr: String? = null,
        @RequestBody body: ApiExecFuncRequest
    ): ResponseEntity<ApiExecFuncResponse> { // prob 202

        //FIXME should be  a service
        val funcId = FuncId(SnowflakeId(stringFuncId.replace("'", "").toLong()))


        val result = request(
            InvokeOneshot(
                execId = generateDomainId(::ExecId),
                correlationId = CorrelationId(correlationIdStr ?: "__default__"), //FIXME
                inputs = InvocationInputs(TableValue()),
                secrets = InvocationSecrets(listOf()),
                funcId = funcId
            )
        )

        return ResponseEntity.ok(
            ApiExecFuncResponse(
                cmdId = CmdId(0)
            )
        )
    }
}