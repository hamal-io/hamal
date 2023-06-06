package io.hamal.backend.web

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.repository.api.domain.Func
import io.hamal.backend.repository.api.domain.Tenant
import io.hamal.backend.req.InvokeOneshot
import io.hamal.backend.req.Request
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.backend.service.cmd.FuncCmdService
import io.hamal.backend.service.query.FuncQueryService
import io.hamal.lib.common.Shard
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.port.GenerateDomainId
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
    @Autowired val eventEmitter: EventEmitter,
    @Autowired val request: Request,
    @Autowired val generateDomainId: GenerateDomainId
) {
    @PostMapping("/v1/funcs")
    fun createFunc(
        @RequestAttribute shard: Shard,
        @RequestAttribute tenant: Tenant,
        @RequestBody req: ApiCreateFuncRequest
    ): Func {
        // FIXME to ApiCreateFuncResponse
        //FIXME as request
        return funcCmdService.create(
            CmdId(0), FuncCmdService.ToCreate(
                funcId = generateDomainId(Shard(1), ::FuncId),
                name = req.name,
                inputs = FuncInputs(listOf()),
                secrets = FuncSecrets(listOf()),
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

        return ResponseEntity.ok(
            ApiListFuncResponse(
                result.map {
                    ApiListFuncResponse.Func(
                        id = it.id,
                        name = it.name
                    )
                }
            )
        )
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
                execId = generateDomainId(Shard(1), ::ExecId),
                correlationId = CorrelationId(correlationIdStr ?: "__default__"), //FIXME
                inputs = InvocationInputs(listOf()),
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