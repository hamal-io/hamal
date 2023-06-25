package io.hamal.backend.instance.web

import io.hamal.backend.instance.component.SystemEventEmitter
import io.hamal.backend.instance.req.InvokeOneshot
import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.cmd.FuncCmdService
import io.hamal.backend.instance.service.query.FuncQueryService
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.port.GenerateDomainId
import io.hamal.lib.script.api.value.TableValue
import io.hamal.lib.sdk.domain.ApiCreateFuncRequest
import io.hamal.lib.sdk.domain.ApiExecFuncRequest
import io.hamal.lib.sdk.domain.ApiExecFuncResponse
import io.hamal.lib.sdk.domain.ApiListFuncResponse
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
    @PostMapping("/v1/funcs")
    fun createFunc(
        @RequestBody req: ApiCreateFuncRequest
    ): Func {
        // FIXME to ApiCreateFuncResponse
        //FIXME as request
        return funcCmdService.create(
            CmdId(0), FuncCmdService.ToCreate(
                funcId = generateDomainId(::FuncId),
                name = req.name,
                inputs = req.inputs,
                secrets = req.secrets,
                code = req.code
            )
        )
    }

    @GetMapping("/v1/funcs")
    fun listFunc(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") stringFuncId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiListFuncResponse> {
        val result = queryService.list(
            afterId = FuncId(SnowflakeId(stringFuncId.toLong())),
            limit = limit
        )


        val b = ApiListFuncResponse(
            result.map {
                ApiListFuncResponse.Func(
                    id = it.id,
                    name = it.name
                )
            }
        )

        return ResponseEntity.ok(b)
    }

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